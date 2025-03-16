package sues.xmz.diploma.diploma_dailyexercisehealthmanagementsystem.test03;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sues.xmz.diploma.common.utils.PasswordUtil;

@SpringBootTest
public class BCryptTest {
    @Test
    public void test() {
//        String master = PasswordUtil.encodePassword("master");
        String master = PasswordUtil.encodePassword("88888888");
        System.out.println(master);
    }
}
