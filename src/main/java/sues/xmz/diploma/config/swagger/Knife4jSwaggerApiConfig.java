package sues.xmz.diploma.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
//@EnableKnife4j
public class Knife4jSwaggerApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("0", ".");
        return new OpenAPI()
                .info(new Info()
                        .title("日常运动健康管理系统API")
                        .description("""
                                本系统旨在为健身爱好者、康复患者、慢性病患者等提供一站式的健康管理平台，
                                通过用户健康状况监测、运动计划制定、饮食建议、进度跟踪等功能，帮助用户科学管理日常运动与健康，提升生活质量。
                                """)
                        .version("2025年01月")
                        .termsOfService("API服务条款")
                        .summary("""
                                ● 目标群体：健身爱好者、康复患者、慢性病患者等需要日常运动和健康管理的用户。
                                ● 主要功能：
                                  ○ 用户健康状况监测
                                  ○ 运动计划制定
                                  ○ 饮食建议
                                  ○ 进度跟踪
                                """)
                        .license(new License()
                                .name("徐铭泽")
                                .url("www.xxxxxxx.com"))
                        .contact(new Contact()
                                .url("123456")
                                .name("028121167徐铭泽")
                                .extensions(extensions))
                )

                .servers(List.of(
                                new Server().url("http://localhost:21167").description("当前的服务端（后端）地址")
                        )
                )

                ;
    }

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
    /*@Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags()!=null){
                openApi.getTags().forEach(tag -> {
                    Map<String,Object> map=new HashMap<>();
                    map.put("x-order", RandomUtil.randomInt(0,100));
                    tag.setExtensions(map);
                });
            }
            if(openApi.getPaths()!=null){
                openApi.addExtension("x-test123","333");
                openApi.getPaths().addExtension("x-abb",RandomUtil.randomInt(1,100));
            }

        };

    }*/


/*    @Bean
    public GroupedOpenApi tweetsOpenApi(@Value("${springdoc.version}") String appVersion) {
        String[] paths = { "/tweets/**" };
        return GroupedOpenApi.builder().
                group("tweets")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Tweets Api").version(appVersion)))
                .pathsToMatch(paths)
                .build();
    }*/

/*    @Bean
    public GroupedOpenApi streamOpenApi(@Value("${springdoc.version}") String appVersion) {
        String[] paths = { "/stream/**" };
        String[] packagedToMatch = { "com.github.xiaoymin" };
        return GroupedOpenApi.builder().group("x-stream")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Stream API").version(appVersion)))
                .pathsToMatch(paths).packagesToScan(packagedToMatch)
                .build();
    }*/
}
