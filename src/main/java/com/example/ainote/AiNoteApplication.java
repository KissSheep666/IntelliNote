package com.example.ainote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 整个项目的启动入口。
 * 运行这个 main 方法,Spring Boot 会自动启动内嵌的 Tomcat,
 * 然后我们的各个 Controller 就能对外提供 HTTP 接口了。
 */
@SpringBootApplication
public class AiNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiNoteApplication.class, args);
    }
}
