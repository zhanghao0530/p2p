package com.bjpowernode.p2p.service.loan;/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/3/19 18:41
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：章昊
 * 2020/3/19
 */
@Component
@Service(interfaceClass = IncomeRecordService.class,version = "1.0.0",timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;


    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public void generateIncomePlan(int productStatus) {

        //获取产品状态为1已满标的产品-> 返回List<已满标产品>
        List<LoanInfo> loanInfoList=loanInfoMapper.selectLoanInfoListByProductStatus(1);
        //循环遍历List<已满标产品>,获取到每一个产品
        for (LoanInfo loanInfo : loanInfoList) {


            //获取当前满标产品的所有投资记录->返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectAllBidInfoByLoanId(loanInfo.getId());

            //循环遍历List<投资记录>,获取到每条投资记录
            for (BidInfo bidInfo : bidInfoList) {


                //将当前的投资记录生成对应的收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setIncomeStatus(0);  //0未返还，1已返还
//                    incomeRecord.setIncomeDate();
//                    incomeRecord.setIncomeMoney();


                //收益时间(date)=满标时间(date)+产品周期(int)天|月
                //收益金额= 投资金额 * 利率 *  周期

                Date incomeDate = null;
                Double incomeMoney=null;
                //判断产品类型
                if(Constants.PRODUCT_TYPE_X.equals(loanInfo.getProductType())){
                    //新手宝
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate()/100/365) * loanInfo.getCycle();
                }else {
                    //优选散标
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate()/100/365)*30 * loanInfo.getCycle();
                }

                incomeMoney=Math.round(incomeMoney*Math.pow(10, 2))/ Math.pow(10, 2);
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);
                incomeRecordMapper.insertSelective(incomeRecord);
            }
            //更新当前产品的状态为2满标且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);



        }


    }

    @Override
    public void generateIncomeBack() {
        //查询收益记录状态为0且收益时间与当前时间一致的收益计划-> 返回List<收益计划>
        List<IncomeRecord>incomeRecordsList=incomeRecordMapper.selectIncomeRecordListByIncomeStatusAndCurdate(0);
        Map<String,Object>paramMap = new HashMap<>();
        //循环遍历List<收益计划>,获取到每-条收益计划
        for (IncomeRecord incomeRecord : incomeRecordsList) {
            paramMap.put("uid", incomeRecord.getUid());
            paramMap.put("bidMoney", incomeRecord.getBidMoney());
            paramMap.put("incomeMoney", incomeRecord.getIncomeMoney());

            //将对应的投资本金和收益返还给对应的帐户
            financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            //将当前收益的状态更新为1
            IncomeRecord updateIncome = new IncomeRecord();
            updateIncome.setIncomeStatus(1);
            updateIncome.setId(incomeRecord.getId());
            incomeRecordMapper.updateByPrimaryKeySelective(updateIncome);
        }

    }
}
