package com.bjpowernode.p2p.web;/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/16 16:30
 * @author:zh
 */

import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：章昊
 * 2020/3/16
 */
@Controller
public class UserController {

    private UserService userService;

    @RequestMapping("/loan/page/register")
    public String register(){
        //{ "code" :-1, "message": "13700000000被占用了" , "success" :false}
        //
        // { "code" :1, "suCcess" : true }
        return "register";
    }

    @RequestMapping("/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(){
        Map<String,Object>map=new HashMap<>();
        map.put("code", 1);
        return map;
    }

}
