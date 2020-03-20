package com.bjpowernode.p2p.mapper.loan;


import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    //

    /**
     * 查询收益记录状态为0且收益时间与当前时间一致的收益计划
     * @param incomeStatus
     * @return
     */

    List<IncomeRecord> selectIncomeRecordListByIncomeStatusAndCurdate(Integer incomeStatus);
}