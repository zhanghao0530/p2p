package com.bjpowernode.p2p.mapper.loan;


import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.List;

public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    /**
     * 修改订单状态
     * @param rechargeRecord
     * @return
     */
    int updateRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 根据充值状态查询订单
     * @param s
     * @return
     */
    List<RechargeRecord> selectByRechargeStatus(String s);

    RechargeRecord selectRechargeRecordByRechargeNo(String rechargeNo);
}