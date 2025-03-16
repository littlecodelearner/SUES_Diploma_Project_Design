package sues.xmz.diploma.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.users.PasswordChangeFailureException;
import sues.xmz.diploma.common.exception.users.UserInfoUpdateException;
import sues.xmz.diploma.common.exception.users.UserNotFoundException;
import sues.xmz.diploma.common.exception.users.UserRegisterFailureException;
import sues.xmz.diploma.domain.req.users.PasswordChangeReq;
import sues.xmz.diploma.domain.req.users.UserInfoUpdateReq;
import sues.xmz.diploma.domain.req.users.UserLoginReq;
import sues.xmz.diploma.domain.req.users.UserRegisterReq;
import sues.xmz.diploma.domain.resp.users.UserInfoResp;
import sues.xmz.diploma.domain.resp.users.UserInfoRespResult;
import sues.xmz.diploma.service.UsersService;

@Slf4j
@Tag(name = "01用户相关业务功能", description = "用户相关的API")
@RequestMapping("/users")
@RestController
public class UsersController {

    @Resource
    private UsersService usersService;

    @ApiOperationSupport(order = 1)
    @PostMapping("/register")
    @Operation(
            summary = "用户注册_前端已实现",
            description = "用户注册账号以及其他信息",
            tags = {".00用户登录与注册"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "注册成功"),
                    @ApiResponse(responseCode = "1002", description = "用户账号已存在"),
                    @ApiResponse(responseCode = "1003", description = "邮箱已经被注册"),
                    @ApiResponse(responseCode = "1031", description = "注册失败"),
            }
//            deprecated = true,
//            hidden = true
    )
    public Result<?> register(@RequestBody @Valid UserRegisterReq userRegisterReq) {
        boolean success = usersService.register(userRegisterReq);
        if (!success) {
            throw new UserRegisterFailureException();
        }
        return Result.success("注册成功");
    }

    @ApiOperationSupport(order = 2)
    @PostMapping("/login")
    @Operation(
            summary = "用户登录_前端已实现",
            tags = {".00用户登录与注册"},
            description = "使用账号和密码进行登录",
            responses = {
                    @ApiResponse(responseCode = "200", description = "登录成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfoRespResult.class))
                    ),
                    @ApiResponse(responseCode = "1000", description = "用户账号不存在"
                    ),
                    @ApiResponse(responseCode = "1001", description = "密码输入错误"),
            }
    )
    public Result<?> login(@RequestBody @Valid UserLoginReq userLoginReq) {
        return Result.success(usersService.login(userLoginReq));
    }

    @ApiOperationSupport(order = 3)
    @GetMapping("/getInfo")
    @Operation(
            summary = "获取用户信息_前端已实现",
            tags = {".02登录后用户信息修改"},
            description = "使用账号和密码进行登录",
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取用户信息成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfoRespResult.class))
                    ),
                    @ApiResponse(responseCode = "1000", description = "用户不存在"),
            }
    )
    public Result<?> getUserInfoByUserId(@RequestParam @Parameter(name = "userId", description = "用户ID")
                                             Integer userId
    ) {
        UserInfoResp userInfoResp = BeanUtil.copyProperties(usersService.getById(userId), UserInfoResp.class);
        if (userInfoResp == null) {
            throw new UserNotFoundException();
        }
        return Result.success(userInfoResp);
    }


    @ApiOperationSupport(order = 4)
    @Operation(
            summary = "更新用户信息_前端已实现",
            tags = {".02登录后用户信息修改"},
            description = "更新个人的一些基本信息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "个人信息更新成功"),
                    @ApiResponse(responseCode = "1003", description = "邮箱已经被注册"),
                    @ApiResponse(responseCode = "1017", description = "更新用户信息失败"),
            }
    )
    @PutMapping("/updateInfo")
    public Result<?> updateInfo(@RequestBody @Valid UserInfoUpdateReq userInfoUpdateReq) {
        boolean success = usersService.updateInfo(userInfoUpdateReq);
        if (!success) {
            throw new UserInfoUpdateException();
        }
        return Result.success("个人信息更新成功");
    }

    @ApiOperationSupport(order = 5)
    @Operation(
            summary = "修改密码_前端已实现",
            tags = {".02登录后用户信息修改"},
            description = "输入旧密码和新密码",
            responses = {
                    @ApiResponse(responseCode = "200", description = "修改密码成功"),
                    @ApiResponse(responseCode = "1000", description = "用户不存在"),
                    @ApiResponse(responseCode = "1004", description = "旧密码输入错误"),
                    @ApiResponse(responseCode = "1032", description = "修改密码失败"),
            }
    )
    @PutMapping("/changePwd")
    public Result<?> changePassword(@RequestBody @Valid PasswordChangeReq passwordChangeReq) {
        boolean success = usersService.changePassword(passwordChangeReq);
        if (!success) {
            throw new PasswordChangeFailureException();
        }
        return Result.success("修改密码成功");
    }

}
