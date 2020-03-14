package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {
    Double queryHistoryAvgerageRate();

    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);
}
