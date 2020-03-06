package com.example.helloui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public String hello() {
        String message = helloService.hello();
        return message;
    }

    @GetMapping("/slow")
    public String slow() {
        String message = helloService.slow();
        return message;
    }
}
