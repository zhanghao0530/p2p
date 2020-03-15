package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVo;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {
    Double queryHistoryAvgerageRate();

    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    PaginationVo<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap);

    LoanInfo queryLoanInfoById(Integer id);

}
