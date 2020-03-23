package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.Map;

/**
 * ClassName:RechargeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/3/20 17:23
 * @author:zh
 */
public interface RechargeRecordService {
    /**
     * 处理掉单
     */
    void dealRechargeRecord() throws Exception;

    /**
     * 生成充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 修改订单状态
     * @param rechargeRecord
     */
    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 用户充值
     * @param paramMap
     */
    void reCharge(Map<String, Object> paramMap) throws Exception;

    /**
     * 通过订单号查询充值记录详情
     * @param rechargeNo
     * @return
     */
    RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo);
}
