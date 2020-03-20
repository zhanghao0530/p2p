package com.bjpowernode.p2p.web;/**
 * ClassName:RechargeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/19 20:24
 * @author:zh
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者：章昊
 * 2020/3/19
 */
@Controller
public class RechargeRecordController {

    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge(){
        return "toRecharge";
    }

    @RequestMapping("/loan/toAlipayRecharge")
    public void toAlipayRecharge(HttpServletRequest request,
                                 @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){
        System.out.println("------------");
    }


    @RequestMapping("/loan/toWxpayRecharge")
    public void toWxpayRecharge(HttpServletRequest request,
                                 @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){
        System.out.println("------------");
    }
}
