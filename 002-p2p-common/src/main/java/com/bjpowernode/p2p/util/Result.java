package com.bjpowernode.p2p.util;/**
 * ClassName:Result
 * Package:com.bjpowernode.p2p.util
 * Description:平台响应对象
 *
 * @date:2020/3/16 18:56
 * @author:zh
 */

import java.util.HashMap;

/**
 * 作者：章昊
 * 2020/3/16
 */
public class Result extends HashMap<String,Object> {
    public static Result success(){
        Result result=new Result();
        result.put("code", 1);
        result.put("success", true);
        return result;
    }

    public static Result error(String message){
        Result result = new Result();
        result.put("code", -1);
        result.put("success", false);
        result.put("message", message);

        return result;
    }

    public static Result success(String data){
        Result result=new Result();
        result.put("code", 1);
        result.put("success", true);
        result.put("data", data);
        return result;
    }
}
