package com.example.helloapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final String applicationName;
    private final String port;

    public HelloController(@Value("${spring.application.name}") String applicationName, @Value("${server.port}") String port) {
        this.applicationName = applicationName;
        this.port = port;
    }

    @GetMapping("/hello")
    public String hello() {
        return applicationName + ":" + port;
    }

    @GetMapping("/slow")
    public String slow() {
        int seconds = 3;
        sleep(seconds);
        return "time = " + seconds + "[s]";
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
