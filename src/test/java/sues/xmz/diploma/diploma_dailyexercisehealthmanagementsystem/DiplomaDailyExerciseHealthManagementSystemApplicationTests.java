package sues.xmz.diploma.diploma_dailyexercisehealthmanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.diploma_dailyexercisehealthmanagementsystem.test01.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiplomaDailyExerciseHealthManagementSystemApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testSuccessWithdata() {
        Result<User> result = Result.success(new User(1, "张三"));
        assertEquals(200, result.getCode());
        assertEquals("成功", result.getMessage());
        assertNotNull(result.getData());
    }

    @Test
    public void testSuccessWithoutData() {
        Result<Object> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testFailureWithMessage() {
        Result<Object> result = Result.failure(ResultCode.BAD_REQUEST);
        assertEquals(400, result.getCode());
        assertEquals("请求参数错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testFailureWithCustomMessage() {
        Result<Object> result = Result.failure(ResultCode.UNAUTHORIZED, "请登录后再试");
        assertEquals(401, result.getCode());
        assertEquals("请登录后再试", result.getMessage());
        assertNull(result.getData());
    }

}
