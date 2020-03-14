package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

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
        Double allBidMoney=bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY, allBidMoney);

        //以下数据的共同点是：理财产品 不同点：类型不一样
        //将以下查询看作是一个分页，将使用MySQL数据库中的limit函数
        //根据产品类型获取产品列表（产品类型、页码、每页显示条数）-->返回产品list<产品>
        Map<String,Object>paramMap=new HashMap<>();
        paramMap.put("currentPage", 0);
        //获取新手宝产品：产品类型：0 显示第一页 每页显示一个
        paramMap.put("productType", Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize", 1);
        List<LoanInfo>xLoanInfoList= loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList", xLoanInfoList);
        //获取优选产品：产品类型：1 显示第一页 每页显示4个
        paramMap.put("productType", Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize", 4);
        List<LoanInfo>uLoanInfoList= loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList", uLoanInfoList);

        //获取散标产品：产品类型：2 显示第一页 每页显示8个
        paramMap.put("productType", Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize", 8);
        List<LoanInfo>sLoanInfoList= loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList", sLoanInfoList);

        return "index";
    }
}
