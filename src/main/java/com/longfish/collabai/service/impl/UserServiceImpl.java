package com.longfish.collabai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.mapper.UserMapper;
import com.longfish.collabai.pojo.dto.*;
import com.longfish.collabai.pojo.entity.User;
import com.longfish.collabai.pojo.vo.UserVO;
import com.longfish.collabai.properties.JwtProperties;
import com.longfish.collabai.service.IUserService;
import com.longfish.collabai.util.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.longfish.collabai.constant.CommonConstant.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author longfish
 * @since 2024-05-16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CodeUtil codeUtil;

    @Autowired
    private AESEncryptUtil aesEncryptUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Boolean usernameUniqueCheck(String username) {
        if (!Pattern.compile(USERNAME_CHECK_REGEX).matcher(username).matches()) {
            throw new BizException(StatusCodeEnum.USERNAME_FORMAT_ERROR);
        }
        return lambdaQuery(User.builder().username(username).build()).exists();
    }

    @Override
    public String login(LambdaLoginDTO lambdaLoginDTO) {
        log.info("用户 {} 登录 @ {}", lambdaLoginDTO, LocalDateTime.now());

        if (lambdaLoginDTO.getUsername() == null || lambdaLoginDTO.getUsername().equals("")) {
            throw new BizException(StatusCodeEnum.USER_IS_NULL);
        }

        boolean isPhone = PhoneUtil.isValid(lambdaLoginDTO.getUsername());
        boolean isEmail = EmailUtil.isValid(lambdaLoginDTO.getUsername());

        if (!isPhone && !isEmail && !usernameUniqueCheck(lambdaLoginDTO.getUsername())) {
            throw new BizException(StatusCodeEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        User query = User.builder().password(DigestUtils.md5DigestAsHex(lambdaLoginDTO.getPassword().getBytes())).build();
        if (isPhone) query.setPhone(lambdaLoginDTO.getUsername());
        else if (isEmail) query.setEmail(lambdaLoginDTO.getUsername());
        else query.setUsername(lambdaLoginDTO.getUsername());

        User result = lambdaQuery(query).one();
        if (result == null) {
            throw new BizException(StatusCodeEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        result.setIpAddress(ipAddress);
        result.setIpSource(ipSource);
        result.setLastLoginTime(LocalDateTime.now());
        updateById(result);

        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, result.getId());

        return JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);
    }

    @Override
    public String codeLogin(LambdaCodeLoginDTO lambdaCodeLoginDTO) {
        log.info("用户 {} 登录 @ {}", lambdaCodeLoginDTO, LocalDateTime.now());

        if (lambdaCodeLoginDTO.getUsername() == null || lambdaCodeLoginDTO.getUsername().equals("")) {
            throw new BizException(StatusCodeEnum.USER_IS_NULL);
        }

        boolean isPhone = PhoneUtil.isValid(lambdaCodeLoginDTO.getUsername());
        boolean isEmail = EmailUtil.isValid(lambdaCodeLoginDTO.getUsername());

        if (!isPhone && !isEmail) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }

        User query = User.builder().build();
        if (isPhone) query.setPhone(lambdaCodeLoginDTO.getUsername());
        else query.setEmail(lambdaCodeLoginDTO.getUsername());
        User result = lambdaQuery(query).one();

        if (result == null) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }

        String code = codeUtil.get(lambdaCodeLoginDTO.getUsername());
        if (code == null || !code.equals(lambdaCodeLoginDTO.getCode())) {
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }

        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        result.setIpAddress(ipAddress);
        result.setIpSource(ipSource);
        result.setLastLoginTime(LocalDateTime.now());
        updateById(result);

        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, result.getId());

        return JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);
    }

    @Override
    public void emailRegister(EmailRegDTO emailRegDTO) {
        if (usernameUniqueCheck(emailRegDTO.getUsername())) {
            throw new BizException(StatusCodeEnum.USER_EXIST);
        }

        if (!Pattern.compile(PASSWORD_CHECK_REGEX).matcher(emailRegDTO.getPassword()).matches()) {
            throw new BizException(StatusCodeEnum.PASSWORD_FORMAT_ERROR);
        }

        String code = codeUtil.get(emailRegDTO.getEmail());
        if (code == null || !code.equals(emailRegDTO.getCode())) {
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }
        if (lambdaQuery().eq(User::getEmail, emailRegDTO.getEmail()).exists()) {
            throw new BizException(StatusCodeEnum.EMAIL_EXIST);
        }

        User save = BeanUtil.copyProperties(emailRegDTO, User.class);
        save.setPassword(DigestUtils.md5DigestAsHex(emailRegDTO.getPassword().getBytes()));
        save.setCreateTime(LocalDateTime.now());
        save.setUpdateTime(LocalDateTime.now());
        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);
        save.setIpAddress(ipAddress);
        save.setIpSource(ipSource);
        save(save);
    }

    @Override
    public void phoneRegister(PhoneRegDTO phoneRegDTO) {
        if (usernameUniqueCheck(phoneRegDTO.getUsername())) {
            throw new BizException(StatusCodeEnum.USER_EXIST);
        }

        if (!Pattern.compile(PASSWORD_CHECK_REGEX).matcher(phoneRegDTO.getPassword()).matches()) {
            throw new BizException(StatusCodeEnum.PASSWORD_FORMAT_ERROR);
        }

        String code = codeUtil.get(phoneRegDTO.getPhone());
        if (code == null || !code.equals(phoneRegDTO.getCode())) {
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }
        if (lambdaQuery().eq(User::getPhone, phoneRegDTO.getPhone()).exists()) {
            throw new BizException(StatusCodeEnum.PHONE_EXIST);
        }

        User save = BeanUtil.copyProperties(phoneRegDTO, User.class);
        save.setPassword(DigestUtils.md5DigestAsHex(phoneRegDTO.getPassword().getBytes()));
        save.setCreateTime(LocalDateTime.now());
        save.setUpdateTime(LocalDateTime.now());
        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);
        save.setIpAddress(ipAddress);
        save.setIpSource(ipSource);
        save(save);
    }

    @Override
    public void code(String username) {
        boolean isPhone = PhoneUtil.isValid(username);
        boolean isEmail = EmailUtil.isValid(username);

        if (!isEmail && !isPhone) {
            throw new BizException(StatusCodeEnum.FORMAT_ERROR);
        }

        String code = codeUtil.getRandomCode();

        // TODO 删去
        code = "114514";

        // TODO 开启
//        Map<String, Object> map = new HashMap<>();
//        map.put("content", "您的验证码为" + code + "，有效期15分钟，请不要告诉他人哦！");
//
//        if (isEmail) {
//            EmailDTO emailDTO = EmailDTO.builder()
//                    .email(username)
//                    .subject("验证码")
//                    .template("code.html")
//                    .commentMap(map)
//                    .code(code)
//                    .build();
//            rabbitTemplate.convertAndSend(RabbitMQConstant.EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
//        } else {
//            SmsDTO smsDTO = SmsDTO.builder()
//                    .phone(username)
//                    .code(code)
//                    .build();
//            rabbitTemplate.convertAndSend(RabbitMQConstant.PHONE_EXCHANGE, "*", new Message(JSON.toJSONBytes(smsDTO), new MessageProperties())
//            );
//        }
    }

    @Override
    public UserVO me() {
        User result = getById(BaseContext.getCurrentId());
        return BeanUtil.copyProperties(result, UserVO.class);

    }

    @Override
    public void forgot(ForgotDTO forgotDTO) {
        boolean isPhone = PhoneUtil.isValid(forgotDTO.getPhoneOrEmail());
        boolean isEmail = EmailUtil.isValid(forgotDTO.getPhoneOrEmail());

        if (!isEmail && !isPhone) {
            throw new BizException(StatusCodeEnum.FORMAT_ERROR);
        }

        if (!Pattern.compile(PASSWORD_CHECK_REGEX).matcher(forgotDTO.getPassword()).matches()) {
            throw new BizException(StatusCodeEnum.PASSWORD_FORMAT_ERROR);
        }

        User query = User.builder().build();
        if (isPhone) query.setPhone(forgotDTO.getPhoneOrEmail());
        else query.setEmail(forgotDTO.getPhoneOrEmail());
        User result = lambdaQuery(query).one();

        if (result == null) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }
        String code = codeUtil.get(forgotDTO.getPhoneOrEmail());
        if (code == null || !code.equals(forgotDTO.getCode())) {
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }

        User update = User.builder()
                .id(query.getId())
                .password(DigestUtils.md5DigestAsHex(forgotDTO.getPassword().getBytes()))
                .updateTime(LocalDateTime.now())
                .build();
        updateById(update);
    }

    @Override
    public void updateInfo(UserInfoDTO userInfoDTO) {
        User update = User.builder().build();

        if (!StringUtils.isBlank(userInfoDTO.getNickname())) {
            update.setNickname(userInfoDTO.getNickname());
        }
        if (!StringUtils.isBlank(userInfoDTO.getAvatar())) {
            update.setAvatar(userInfoDTO.getAvatar());
        }
        if (!StringUtils.isBlank(userInfoDTO.getInfo())) {
            update.setInfo(userInfoDTO.getInfo());
        }
        if (userInfoDTO.getGender() != null && userInfoDTO.getGender() > 0 && userInfoDTO.getGender() < 4) {
            update.setGender(userInfoDTO.getGender());
        }
        if (!StringUtils.isBlank(userInfoDTO.getBirthday())) {
            try {
                update.setBirthday(LocalDate.parse(userInfoDTO.getBirthday()));
            } catch (Exception e) {
                throw new BizException(StatusCodeEnum.DATE_FORMAT_ERROR);
            }
        }
        if (!StringUtils.isBlank(userInfoDTO.getLocation())) {
            update.setLocation(userInfoDTO.getLocation());
        }

        update.setId(BaseContext.getCurrentId());

        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        update.setIpAddress(ipAddress);
        update.setIpSource(ipSource);
        update.setUpdateTime(LocalDateTime.now());

        updateById(update);
    }

    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        User result = getById(BaseContext.getCurrentId());

        if (passwordEditDTO.getNewPassword().equals("")) {
            throw new BizException("密码不能为空");
        }
        if (!DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes()).equals(result.getPassword())) {
            throw new BizException(StatusCodeEnum.PASSWORD_ERROR);
        }

        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        User update = User.builder()
                .id(result.getId())
                .password(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()))
                .updateTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .build();
        updateById(update);
    }

    @Override
    public void editUsername(UsernameDTO usernameDTO) {
        if (!Pattern.compile(USERNAME_CHECK_REGEX).matcher(usernameDTO.getUsername()).matches()) {
            throw new BizException(StatusCodeEnum.USERNAME_FORMAT_ERROR);
        }
        if (usernameUniqueCheck(usernameDTO.getUsername())) {
            throw new BizException(StatusCodeEnum.USER_EXIST);
        }

        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        User update = User.builder()
                .id(BaseContext.getCurrentId())
                .username(usernameDTO.getUsername())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .build();
        updateById(update);
    }

    @Override
    public void bindPhone(PhoneBindDTO phoneBindDTO) {
        if (!PhoneUtil.isValid(phoneBindDTO.getPhone())) {
            throw new BizException(StatusCodeEnum.FORMAT_ERROR);
        }
        User result = getById(BaseContext.getCurrentId());
        if (result.getPhone() != null) {
            throw new BizException(StatusCodeEnum.PHONE_EXIST);
        }
        if (!codeUtil.get(phoneBindDTO.getPhone()).equals(phoneBindDTO.getCode())) {
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }
        if (lambdaQuery().eq(User::getPhone, phoneBindDTO.getPhone()).exists()) {
            throw new BizException(StatusCodeEnum.PHONE_EXIST);
        }
        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        User update = User.builder()
                .id(result.getId())
                .phone(phoneBindDTO.getPhone())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .build();

        updateById(update);
    }

    @Override
    public void bindEmail(EmailBindDTO emailBindDTO) {
        if (!EmailUtil.isValid(emailBindDTO.getEmail())) {
            throw new BizException(StatusCodeEnum.FORMAT_ERROR);
        }
        User result = getById(BaseContext.getCurrentId());
        if (result.getEmail() != null) {
            throw new BizException(StatusCodeEnum.EMAIL_EXIST);
        }

        String code = codeUtil.get(emailBindDTO.getEmail());
        if (code == null || !code.equals(emailBindDTO.getCode())) {
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }
        if (lambdaQuery().eq(User::getEmail, emailBindDTO.getEmail()).exists()) {
            throw new BizException(StatusCodeEnum.EMAIL_EXIST);
        }
        String ipAddress = IpUtil.getIpAddress(request);
        String ipSource = IpUtil.getIpSource(ipAddress);

        User update = User.builder()
                .id(result.getId())
                .email(emailBindDTO.getEmail())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .build();

        updateById(update);
    }
}
