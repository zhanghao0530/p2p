package com.bjpowernode.p2p.mapper.loan;


import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    Double selectAllBidMoney();

    List<BidInfo> selectRecentlyBidInfoByProductId(Map<String, Object> paramMap);
}