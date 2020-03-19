package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidUser;

import java.util.List;
import java.util.Map;

/**
 * ClassName:BidInfoService
 * Package:com.bjpowernode.p2p.service
 * Description:
 *
 * @date:2020/3/14 10:27
 * @author:zh
 */
public interface BidInfoService {
    /**
     * 查询所有投资金额
     * @return
     */
    Double queryAllBidMoney();

    List<BidInfo> queryRecentlyBidInfoByProductId(Map<String, Object> paramMap);

    /**
     * 用户投资
     * @param paramMap
     */
    void invest(Map<String, Object> paramMap) throws Exception;

    /**
     * 用户投资排行
     * @return
     */
    List<BidUser> queryBidUserTop();
}
