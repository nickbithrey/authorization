package org.innovation.authorization;

import java.security.Principal;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/api/v1/user")
    public Principal user(Principal principal) {
        return principal;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).properties("spring.config.name=auth-server").run(args);
    }

}
