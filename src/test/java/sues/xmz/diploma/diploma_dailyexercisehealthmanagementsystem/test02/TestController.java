package sues.xmz.diploma.diploma_dailyexercisehealthmanagementsystem.test02;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.users.PasswordErrorException;
import sues.xmz.diploma.common.exception.users.UserNotFoundException;


@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/success")
    public Result<String> testSuccess() {
        return Result.success("操作成功");
    }

    @GetMapping("/success-no-data")
    public Result<String> testSuccessNoData() {
        return Result.success();
    }

    @GetMapping("/user-not-found")
    public void testUserNotFoundException() {
        throw new UserNotFoundException();
    }

    @GetMapping("/password-error")
    public void testPasswordErrorException() {
        throw new PasswordErrorException();
    }

    @GetMapping("/runtime-exception")
    public void testRuntimeException() {
        throw new RuntimeException("系统内部错误");
    }
}