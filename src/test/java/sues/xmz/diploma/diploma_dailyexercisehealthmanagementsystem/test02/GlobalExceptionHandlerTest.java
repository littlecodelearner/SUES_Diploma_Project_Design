package sues.xmz.diploma.diploma_dailyexercisehealthmanagementsystem.test02;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 测试全局异常处理类
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSuccessWithdata() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("成功")))
                .andExpect(jsonPath("$.data", is("操作成功")));
    }

    @Test
    public void testSuccessWithoutData() throws Exception {
        mockMvc.perform(get("/test/success-no-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("成功")))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    public void testUserNotFoundException() throws Exception {
        mockMvc.perform(get("/test/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(1000)))
                .andExpect(jsonPath("$.message", is("用户账号不存在")))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    public void testPasswordErrorException() throws Exception {
        mockMvc.perform(get("/test/password-error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(1001)))
                .andExpect(jsonPath("$.message", is("用户密码错误")))
                .andExpect(jsonPath("$.data", nullValue()));
    }


    @Test
    public void testRuntimeException() throws Exception {
        mockMvc.perform(get("/test/runtime-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is(500)))
                .andExpect(jsonPath("$.message", is("服务器内部错误")))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    public void testNullPointerException() throws Exception {
        mockMvc.perform(get("/test/null-pointer"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is(500)))
                .andExpect(jsonPath("$.message", is("服务器内部错误")))
                .andExpect(jsonPath("$.data", nullValue()));
    }
}