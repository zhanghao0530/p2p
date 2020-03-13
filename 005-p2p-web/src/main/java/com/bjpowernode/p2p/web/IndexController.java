package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：章昊
 * 2020/3/13
 */
@Controller
public class IndexController {
    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false)
    private LoanInfoService loanInfoService;
    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;

    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model){
        //创建一个固定的线程池
       /* ExecutorService executorService= Executors.newFixedThreadPool(100);

        for (int i = 0; i < 10000; i++) {
            //开启一个线程
            executorService.submit(new Runnable(){
                @Override
                public void run() {
                    Double historyAvgerageRate = loanInfoService.queryHistoryAvgerageRate();
                    model.addAttribute(Constants.HISTORY_AVERAGE_RATE, historyAvgerageRate);
                }
            });
        }

        executorService.shutdown();*/


        //获取平台历 史平均年化收益率
        Double historyAvgerageRate = loanInfoService.queryHistoryAvgerageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE, historyAvgerageRate);

        //获取平台注册总人数
        Long allUserCount=userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT, allUserCount);
        //获取平台累计投资金额

        //获取新手宝产品

        //获取优选产品

        //获取散标产品



        return "index";
    }
}
