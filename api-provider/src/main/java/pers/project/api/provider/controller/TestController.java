package pers.project.api.provider.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import pers.project.api.provider.model.Test;


/**
 * 接口测试控制器
 *
 * @author Luo Fei
 * @date 2023/2/25
 */
@RestController
public class TestController {

    @GetMapping("/test/get")
    public String get(@RequestParam String test, HttpServletRequest request) {
        return test;
    }

    @PostMapping("/test/post")
    public String post(@RequestBody Test test, HttpServletRequest request) {
        return test.getTest();
    }

}
