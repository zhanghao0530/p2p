package com.bjpowernode.p2p.web;/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/16 16:30
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import com.bjpowernode.p2p.util.HttpClientUtils;
import com.bjpowernode.p2p.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 作者：章昊
 * 2020/3/16
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;

    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false)
    private RedisService redisService;
    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false)
    private FinanceAccountService financeAccountService;

    @RequestMapping("/loan/page/register")
    public String register(){

        return "register";
    }

    @RequestMapping("/loan/page/realName")
    public String toRealName(){
        return "realName";
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
                           @RequestParam(value = "messageCode",required = true)String messageCode,
                           HttpServletRequest request){

        try {
            //从redis中获取短信验证码
            String redisMessageCode=redisService.get(phone);

            //判断用户输入的验证码是否正确
            if(!StringUtils.equals(redisMessageCode, messageCode)){
                return Result.error("请输入正确的验证码");
            }

            //用户注册[1.新增用户，2.新增账户]-->返回user

            User user=userService.register(phone,loginPassword);


            //将用户信息保存到session中
            request.getSession().setAttribute(Constants.SESSION_USER, user);
        } catch (Exception e) {
            e.printStackTrace();

            return Result.error("短信平台繁忙，请稍候再试");
        }


        return Result.success(messageCode);
    }

    @RequestMapping("/loan/messageCode")
    @ResponseBody
    public Result messageCode(@RequestParam(value = "phone",required = true)String phone){

        String messageCode ="";

        try {


            Map<String,Object>paramMap=new HashMap<>();
            paramMap.put("appkey", "");
            paramMap.put("mobile", phone);

            messageCode = this.getRandomNumber(6);

            paramMap.put("content", "【凯信通】您的验证码是："+messageCode);

            //发短信，调用接口，得到的json格式字符串
            //String jsonString =HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);


            //模拟报文
            String jsonString="{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 0,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-1111611</remainpoint>\\n <taskID>101609164</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                    "}";
            System.out.println("106短信接口响应参数"+jsonString);
            //解析json格式的字符串，使用fastJson来解析
            //将Json格式的字符串转换为json格式对象
            JSONObject jsonObject=JSONObject.parseObject(jsonString);

            //获取通信表示CODE
            String code=jsonObject.getString("code");

            //判断是否通信成功
            if(!StringUtils.equals("10000", code)){
                return Result.error("通信异常");
            }
            //如果成功，获取result对应的xml格式的字符串
            String resultXmlString = jsonObject.getString("result");

            //通过dom4j+xmlpath来解析xml
            Document document =DocumentHelper.parseText(resultXmlString);
            //获取returnstatus节点的内容
            Node node=document.selectSingleNode("//returnstatus");
            //获取节点的文本
            String returnstatus = node.getText();
            //判断是否发送成功
            if(!StringUtils.equals("Success", returnstatus)){
                return Result.error("短信发送失败");
            }

            //如果成功，将该随机数存入redis中
            redisService.put(phone,messageCode);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("短信平台异常");
        }
        return Result.success(messageCode);
    }

    //生成随机数
    private String getRandomNumber(int count) {
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < count; i++) {
            int index= (int) Math.round(Math.random()*9);
            sb.append(index);
        }
        return sb.toString();
    }

    @RequestMapping("/loan/realName")
    @ResponseBody
    public Result realName(@RequestParam(value = "phone",required = true)String phone,
                           @RequestParam(value = "idCard",required = true)String idCard,
                           @RequestParam(value = "realName",required = true)String realName,
                           @RequestParam(value = "messageCode",required = true)String messageCode,
                           HttpServletRequest request){


        try {

            //从redis中获取短信验证码
            String redisMessageCode = redisService.get(phone);
            System.out.println(redisMessageCode);
            if (!StringUtils.equals(redisMessageCode, messageCode)) {
                return Result.error("请输入正确的短信验证码");
            }

            Map<String,Object>paramMap=new HashMap<>();
            paramMap.put("appkey", "");
            paramMap.put("cardNo", idCard);
            paramMap.put("realName", realName);
            //调用接口
            //String jsonString=HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);
            String jsonString="{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \"乐天磊\",\n" +
                    "            \"idcard\": \"350721197702134399\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            //将json格式的字符串转换为json对象
            JSONObject jsonObject=JSONObject.parseObject(jsonString);

            //获取通信标识code
            String code=jsonObject.getString("code");


//            判断通信是否成功
            if(!StringUtils.equals("10000", code)){
                return Result.error("通讯异常");
            }
//            获取isok处理结果
            boolean isok=jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");


            if(!isok){
                return Result.error("身份证信息与真实姓名不匹配");
            }

            //从session中获取用户信息
            User sessionUser= (User) request.getSession().getAttribute(Constants.SESSION_USER);

            //将真实姓名和身份证号码更新到用户信息
            User updateUser = new User();
            updateUser.setId(sessionUser.getId());
            updateUser.setName(realName);
            updateUser.setIdCard(idCard);
            int count=userService.modifyUserById(updateUser);
            if(count<=0){
                return Result.error("用户信息更新失败");
            }

            User user=userService.queryUserByPhone(phone);
            //更新session中用户信息
            request.getSession().setAttribute(Constants.SESSION_USER, user);


        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("实名认证失败");
        }
        return Result.success();
    }

    @RequestMapping("/loan/myFinanceAccount")
    @ResponseBody
    public FinanceAccount financeAccount(HttpServletRequest request){
        //从session中获取user
        User sessionUser= (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取账户信息
        FinanceAccount financeAccount=financeAccountService.queryFinaceAccountByUid(sessionUser.getId());
        return financeAccount;
    }

    /**
     * 退出登录
     */
    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request){

        //清除session
        request.getSession().invalidate();
//        request.getSession().removeAttribute(Constants.SESSION_USER);
        return "redirect:/index";
    }

    //跳转到登录页面
    @RequestMapping("/loan/page/login")
    public String pageLogin(HttpServletRequest request, Model model,
                            @RequestParam(value = "localPageUrl",required = true)String localPageUrl){

        model.addAttribute("redirectUrl", localPageUrl);
        return "login";
    }

    @PostMapping("/loan/login")
    public @ResponseBody Result login(@RequestParam(value = "phone",required = true)String phone,
                                      @RequestParam(value = "loginPassword",required = true)String loginPassword,
                                      @RequestParam(value = "messageCode",required = true)String messageCode,
                                     HttpServletRequest request){


        try {
            //验证验证码
            String redisMessageCode =redisService.get(phone);
            if(!StringUtils.equals(redisMessageCode, messageCode)){
                return Result.error("短信验证码不正确");
            }

            //通过手机号和密码验证是否正确，如果正确则修改最近登录时间

            User user=userService.login(phone,loginPassword);
            request.getSession().setAttribute(Constants.SESSION_USER, user);




        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("用户登录异常");
        }
        return Result.success();
    }

    @RequestMapping("/loan/myCenter")
    public String myCenter(HttpServletRequest request,Model model){


        //获取session中的user对象
        User sessionUser= (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户 标识获取帐户信息
        FinanceAccount financeAccount=financeAccountService.queryFinaceAccountByUid(sessionUser.getId());
        model.addAttribute("financeAccount", financeAccount) ;
        //以下三个由大家自己完成作业
        //根据 用户标识获取最近投资记录
        //根据用户标识获取最近充值 记录
        //根据用户标识获取最近收益记录


        return "myCenter";
    }

}






























