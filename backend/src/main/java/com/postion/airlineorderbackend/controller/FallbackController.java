package com.postion.airlineorderbackend.controller;

import org.springframework.stereotype.Controller;

@Deprecated
@Controller
public class FallbackController {

    // @GetMapping({"/", "/login", "/orders/**"}) // 覆盖所有前端路由
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
