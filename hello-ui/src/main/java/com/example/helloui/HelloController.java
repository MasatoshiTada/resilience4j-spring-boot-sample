package com.example.helloui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "ボタンをどれかクリックしてください");
        return "index";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        String message = helloService.hello();
        model.addAttribute("message", message);
        return "index";
    }

    @GetMapping("/slow")
    public String slow(Model model) {
        String message = helloService.slow();
        model.addAttribute("message", message);
        return "index";
    }
}
