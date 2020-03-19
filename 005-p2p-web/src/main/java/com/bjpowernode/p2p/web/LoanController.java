package com.bjpowernode.p2p.web;/**
 * ClassName:LoanController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/14 20:57
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：章昊
 * 2020/3/14
 */
@Controller
public class LoanController {
    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false)
    private FinanceAccountService financeAccountService;

    @RequestMapping("/loan/loan")
    public String loan(Model model, HttpServletRequest request,
                       @RequestParam(value = "ptype",required = false)Integer ptype,
                       @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage){
        ////根据产品类型分页查询产品列表（产品类型，页码，每页显示条数）-> 返回数据(每页显示数据,总条数)
        Map<String,Object>paramMap =new HashMap<>();
        if(ObjectUtils.allNotNull(ptype)){
            paramMap.put("productType", ptype);
        }
        int pageSize=9;
        paramMap.put("pageSize", pageSize);
        paramMap.put("currentPage", (currentPage-1)*pageSize);
        PaginationVo<LoanInfo> paginationVo=loanInfoService.queryLoanInfoListByPage(paramMap);

        //计算总页数
        int totalPage = paginationVo.getTotal().intValue()/pageSize;
        int mod = paginationVo.getTotal().intValue()%pageSize;
        if(mod>0){
            totalPage=totalPage+1;
        }
        model.addAttribute("loanInfoList", paginationVo.getDataList());
        model.addAttribute("totalRows", paginationVo.getTotal());
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("ptype",ptype);
        }




        //投资排行榜
        List<BidUser>bidUserList=bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserList", bidUserList);

        return "loan";
    }

    @RequestMapping("/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                           @RequestParam(value = "id",required = true)Integer id){
        //根据产品Id查询详情
        LoanInfo loanInfo=loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo", loanInfo);
        Map<String,Object>paramMap=new HashMap<>();
        paramMap.put("loanId", id);
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 10);
        //根据产品Id查询最近前10条投资记录
        List<BidInfo> bidInfoList=bidInfoService.queryRecentlyBidInfoByProductId(paramMap);
        model.addAttribute("bidInfoList", bidInfoList);

        //查询session中user对象判断用户是否登录
        User sessionUser= (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if(ObjectUtils.allNotNull(sessionUser)){

            //查询账户可用余额
            FinanceAccount financeAccount=financeAccountService.queryFinaceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount", financeAccount);
        }

        return "loanInfo";
    }
}
