package com.longfish.collabai.controller;


import com.longfish.collabai.annotation.AccessLimit;
import com.longfish.collabai.annotation.NoLogin;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.dto.*;
import com.longfish.collabai.pojo.vo.LoginVO;
import com.longfish.collabai.pojo.vo.UserVO;
import com.longfish.collabai.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.longfish.collabai.constant.CommonConstant.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author longfish
 * @since 2024-05-16
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户相关")
public class UserController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "用户名唯一性检验")
    @GetMapping("/uniqueCheck")
    @NoLogin
    public Result<?> uniqueCheck(String username) {
        return userService.usernameUniqueCheck(username) ? Result.error(StatusCodeEnum.USER_EXIST) : Result.success();
    }

    @Operation(summary = "使用密码登录")
    @PostMapping("/login")
    @NoLogin
    public Result<LoginVO> login(@RequestBody LambdaLoginDTO lambdaLoginDTO) {
        return Result.success(LoginVO.builder().jwt(userService.login(lambdaLoginDTO)).build());
    }

    @Operation(summary = "使用验证码登录")
    @PostMapping("/login/code")
    @NoLogin
    public Result<LoginVO> loginByCode(@RequestBody LambdaCodeLoginDTO lambdaCodeLoginDTO) {
        return Result.success(LoginVO.builder().jwt(userService.codeLogin(lambdaCodeLoginDTO)).build());
    }

    @Operation(summary = "退出登录", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @GetMapping("/logout")
    public Result<?> logout() {
        BaseContext.removeCurrent();
        return Result.success();
    }

    @Operation(summary = "发送验证码")
    @GetMapping("/code")
    @AccessLimit(seconds = 5, maxCount = 1)
    @NoLogin
    public Result<?> code(String username) {
        userService.code(username);
        return Result.success();
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    @NoLogin
    public Result<?> lambdaRegister(@RequestBody PhoneRegDTO phoneRegDTO) {
        userService.phoneRegister(phoneRegDTO);
        return Result.success();
    }

    @Operation(summary = "我的信息", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.me());
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgot")
    @NoLogin
    public Result<?> forgot(@RequestBody PhoneForgotDTO phoneForgotDTO) {
        ForgotDTO forgotDTO = ForgotDTO.builder()
                .phoneOrEmail(phoneForgotDTO.getPhone())
                .code(phoneForgotDTO.getCode())
                .password(phoneForgotDTO.getPassword())
                .build();
        userService.forgot(forgotDTO);
        return Result.success();
    }

    @Operation(summary = "修改用户信息", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @PutMapping("/info")
    public Result<?> update(@RequestBody UserInfoDTO userInfoDTO) {
        userService.updateInfo(userInfoDTO);
        return Result.success();
    }

    @Operation(summary = "修改密码", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @PutMapping("/password")
    public Result<?> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        userService.editPassword(passwordEditDTO);
        return Result.success();
    }

    @Operation(summary = "修改用户名", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @PutMapping("/username")
    public Result<?> editUsername(@RequestBody UsernameDTO usernameDTO) {
        userService.editUsername(usernameDTO);
        return Result.success();
    }

    @Operation(summary = "绑定或修改手机", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @PutMapping("/phone")
    public Result<?> bindPhone(@RequestBody PhoneBindDTO phoneBindDTO) {
        userService.bindPhone(phoneBindDTO);
        return Result.success();
    }

    @Operation(summary = "绑定或修改邮箱", parameters = {@Parameter(
            name = TOKEN_NAME, required = true,
            in = ParameterIn.HEADER,
            description = HEADER_ADVICE,
            example = HEADER_VAR)})
    @PutMapping("/email")
    public Result<?> bindEmail(@RequestBody EmailBindDTO emailBindDTO) {
        userService.bindEmail(emailBindDTO);
        return Result.success();
    }

}
