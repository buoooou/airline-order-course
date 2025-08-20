package com.postion.airlineorderbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FallbackController {

    @GetMapping({"/", "/login", "/orders/**"}) // 覆盖所有前端路由
    public String forwardToIndex() {
        return "forward:/index.html"; // 返回静态入口文件
    }
}
