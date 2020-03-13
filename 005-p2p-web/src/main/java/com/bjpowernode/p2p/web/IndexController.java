package com.bjpowernode.p2p.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者：章昊
 * 2020/3/13
 */
@Controller
public class IndexController {



    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model){
        return "index";
    }
}
