package com.bjpowernode.p2p.web;/**
 * ClassName:BidInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/18 22:38
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.util.Result;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：章昊
 * 2020/3/18
 */
@Controller
public class BidInfoController {
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @PostMapping("/loan/invest")
    @ResponseBody
    public  Result invest(@RequestParam(value = "bidMoney",required = true)Double bidMoney,
                          @RequestParam(value = "loanId",required = true)Integer loanId, HttpServletRequest request){


        try {

            User sessionUser  = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            Map<String,Object>paramMap=new HashMap<>();
            paramMap.put("bidMoney", bidMoney);
            paramMap.put("loanId", loanId);
            paramMap.put("uid", sessionUser.getId());
            paramMap.put("phone", sessionUser.getPhone());
            //用户投资[1.更新产品剩余可投金额2.更新帐户可用余额3.新增投资记录4.判断产品是否满标](产品标识，投资金额，用户标识）

            bidInfoService.invest(paramMap);

                //测试超卖
                //新建线程连接池
           /* ExecutorService executorService = Executors.newFixedThreadPool(100);
            Map<String,Object> paramMap = new HashMap<String, Object>();

            for (int i = 0; i < 20000; i++) {
                executorService.submit(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        //准备投资参数
                        paramMap.put("uid",1);
                        paramMap.put("loanId",4);
                        paramMap.put("bidMoney",1.0);

                        //用户投资[1.更新产品剩余可投金额 2.更新帐户可用余额 3.新增投资记录 4.判断产品是否满标](产品标识,投资金额,用户标识)
                        bidInfoService.invest(paramMap);
                    }
                });
            }

            executorService.shutdownNow();*/

        } catch (Exception e) {
            e.printStackTrace();
            return  Result.error("投资失败");
        }
        return Result.success();
    }
}
