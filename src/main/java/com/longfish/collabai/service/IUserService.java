package com.longfish.collabai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longfish.collabai.pojo.dto.*;
import com.longfish.collabai.pojo.entity.User;
import com.longfish.collabai.pojo.vo.UserVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author longfish
 * @since 2024-05-16
 */
public interface IUserService extends IService<User> {

    Boolean usernameUniqueCheck(String username);

    String login(LambdaLoginDTO lambdaLoginDTO);

    String codeLogin(LambdaCodeLoginDTO lambdaCodeLoginDTO);

    void code(String username);

    UserVO me();

    void phoneRegister(PhoneRegDTO phoneRegDTO);

    void emailRegister(EmailRegDTO emailRegDTO);

    void forgot(ForgotDTO forgotDTO);

    void updateInfo(UserInfoDTO userInfoDTO);

    void editPassword(PasswordEditDTO passwordEditDTO);

    void editUsername(UsernameDTO usernameDTO);

    void bindPhone(PhoneBindDTO phoneBindDTO);

    void bindEmail(EmailBindDTO emailBindDTO);

    void setDefaultAvatar(String url);
}
