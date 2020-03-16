package com.bjpowernode.p2p.web;/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/16 16:30
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.user.UserService;
import com.bjpowernode.p2p.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：章昊
 * 2020/3/16
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;

    @RequestMapping("/loan/page/register")
    public String register(){

        return "register";
    }

    @RequestMapping("/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(value = "phone",required = true)String phone){
       /*Map<String, object> retMap = new HashMap<String, 0bject>();

        retMap. put("code" ,1);
        retMap. put("success", true);
        I
        retMap.put("code",1) ;
        retMap . put("message", "");
        retMap.put("success",false);*/
        //{ "code" :-1, "message": "13700000000被占用了" , "success" :false}
        //
        // { "code" :1, "suCcess" : true }
        //封装相应参数格式
        //验证手机号码是否重复
        //返回一个user对象方便后期重复利用
        User user=userService.queryUserByPhone(phone);
        if(ObjectUtils.allNotNull(user)){
            return Result.error("手机号码已被注册，请更换手机号码");
        }
        return Result.success();
    }

    @PostMapping("/loan/register")
    @ResponseBody
    public Object registry(@RequestParam(value = "phone",required = true)String phone,
                           @RequestParam(value = "loginPassword",required = true)String loginPassword,
                           HttpServletRequest request){

        try {
            //用户注册[1.新增用户，2.新增账户]-->返回user

            User user=userService.register(phone,loginPassword);


            //将用户信息保存到session中
            request.getSession().setAttribute(Constants.SESSION_USER, user);
        } catch (Exception e) {
            e.printStackTrace();

            return Result.error("用户注册失败");
        }


        return Result.success();
    }
}
