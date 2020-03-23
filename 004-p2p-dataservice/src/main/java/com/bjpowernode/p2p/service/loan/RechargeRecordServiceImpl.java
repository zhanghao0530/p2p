package com.bjpowernode.p2p.service.loan;/**
 * ClassName:RechargeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/3/20 17:24
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：章昊
 * 2020/3/20
 */
@Component
@Service(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 15000)
@Slf4j
public class RechargeRecordServiceImpl implements RechargeRecordService {
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;



    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {

        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
       return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }
    @Transactional
    @Override
    public void reCharge(Map<String, Object> paramMap) throws Exception {
        //更新账户可用余额
        int updateFinanceAccountCount=financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
        if(updateFinanceAccountCount<=0){
            throw  new  Exception("更新账户可用余额失败");
        }

        //更新充值记录的状态
        RechargeRecord updateRechargeRecord = new RechargeRecord();
        updateRechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
        updateRechargeRecord.setRechargeStatus("1");

        int updateRechargeRecordCount=rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeRecord);
        if(updateRechargeRecordCount<=0){
            throw new Exception("更新充值记录状态失败");
        }
    }

    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo) {
        RechargeRecord rechargeRecord=rechargeRecordMapper.selectRechargeRecordByRechargeNo(rechargeNo);
        return rechargeRecord;
    }

    @Transactional
    @Override
    public void dealRechargeRecord() throws Exception {
        //检查充值状态为0的订单,
        List<RechargeRecord>rechargeRecordList =rechargeRecordMapper.selectByRechargeStatus("0");


        Map<String,Object>paramMap=new HashMap<>();
        //循环遍历获取到每一个订单
        for (RechargeRecord rechargeRecord : rechargeRecordList) {
            paramMap.put("out_trade_no",rechargeRecord.getRechargeNo());
            //调用订单查询接口,返回订单详情
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);


            //将json格式的字符串转换为JSON对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            //获取alipay_trade_query_response对应的json对象
            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

            //获取通信标识
            String code = tradeQueryResponse.getString("code");

            if(!StringUtils.equals("10000", code)){
                log.info("调用订单查询接口,通信异常");
                throw new Exception("调用订单查询接口,通信异常");
            }
            String tradeStatus=tradeQueryResponse.getString("trade_status");

            if(StringUtils.equals("TRADE_CLOSED", tradeStatus)){
                //更新充值记录的状态为2
                RechargeRecord rechargeRecord1 = new RechargeRecord();
                rechargeRecord1.setRechargeNo(rechargeRecord.getRechargeNo());
                rechargeRecord1.setRechargeStatus("2");
                int i=rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord1);
                if(i<=0){
                    log.info("充值订单号为"+rechargeRecord1.getRechargeNo()+",更新状态为2,更新失败");
                    throw new Exception("更新状态为2,失败");
                }
            }

            if(StringUtils.equals("TRADE_SUCCESS", tradeStatus)){
                //查询订单状态
                RechargeRecord rechargeDetail = rechargeRecordMapper.selectRechargeRecordByRechargeNo(rechargeRecord.getRechargeNo());

                if(StringUtils.equals("0", rechargeDetail.getRechargeStatus())){

                    //更新用户账户可用金额,
                    paramMap.put("uid", rechargeRecord.getUid());
                    paramMap.put("rechargeMoney", rechargeRecord.getRechargeMoney());
                    int updateFinanceCount= financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
                    if(updateFinanceCount<=0){
                        log.info("更新账户可用余额失败");
                        throw new Exception("更新账户可用余额失败");
                    }
                    //更新充值记录状态为1
                    RechargeRecord updateRecharge = new RechargeRecord();
                    updateRecharge.setRechargeNo(rechargeRecord.getRechargeNo());
                    updateRecharge.setRechargeStatus("1");
                    int i=rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRecharge);
                    if(i<=0){
                        log.info("更新充值记录为1,失败");
                        throw new Exception("更新充值记录为1失败");
                    }
                }


            }
        }


    }
}
