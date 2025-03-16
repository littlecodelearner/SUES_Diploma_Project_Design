package sues.xmz.diploma;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;

import java.net.InetAddress;

@Slf4j
@Validated
@MapperScan("sues.xmz.diploma.mapper") //https://www.baomidou.com/getting-started/
@SpringBootApplication
public class DiplomaDailyExerciseHealthManagementSystemApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(DiplomaDailyExerciseHealthManagementSystemApplication.class, args);

        // SpringApplication app=new SpringApplication(DiplomaDailyExerciseHealthManagementSystemApplication.class);
        // ConfigurableApplicationContext application=app.run(args);

        Environment env = application.getEnvironment();
        log.info("""

                        ----------------------------------------------------------
                        \t\
                        Application '{}' is running! Access URLs:
                        \t\
                        Local: \t\thttp://localhost:{}
                        \t\
                        External: \thttp://{}:{}
                        \t\
                        Doc: \thttp://{}:{}/doc.html
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

}
