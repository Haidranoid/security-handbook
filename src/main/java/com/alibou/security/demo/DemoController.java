package com.alibou.security.demo;

import com.alibou.security.user.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
@Hidden
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayHello(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var message = "Hello, " + user.getEmail();
        return ResponseEntity.ok(message);
    }
}
