package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:IncomeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/3/19 18:38
 * @author:zh
 */
public interface IncomeRecordService {

    /**
     * 收益记录创建
     * @param productStatus
     */
    void generateIncomePlan(int productStatus);

    /**
     * 收益返还
     *
     */
    void generateIncomeBack();
}
