package com.bjpowernode.p2p.mapper.user;


import com.bjpowernode.p2p.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    FinanceAccount selectfinanceAccountByUid(Integer uid);

    /**
     * 更新用户可用余额
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByBid(Map<String, Object> paramMap);

    /**
     * 根据投资返还更新账户信息
     * @param paramMap
     */
    void updateFinanceAccountByIncomeBack(Map<String, Object> paramMap);

    /**
     * 根据充值结果更新账户可用余额
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByRecharge(Map<String, Object> paramMap);
}