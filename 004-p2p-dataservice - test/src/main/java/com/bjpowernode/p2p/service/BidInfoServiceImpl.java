package com.bjpowernode.p2p.service;/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service
 * Description:
 *
 * @date:2020/3/14 10:28
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 作者：章昊
 * 2020/3/14
 */
@Component
@Service(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double queryAllBidMoney() {

        Double allBidMoney= (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

        if(!ObjectUtils.allNotNull(allBidMoney)){

            //开启同步代码块
            synchronized (this){

                allBidMoney= (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

                if(!ObjectUtils.allNotNull(allBidMoney)){

                    allBidMoney=bidInfoMapper.selectAllBidMoney();

                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY, allBidMoney,15, TimeUnit.MINUTES);
                }
            }
        }


        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryRecentlyBidInfoByProductId(Map<String, Object> paramMap) {
        List<BidInfo> bidInfoList=bidInfoMapper.selectRecentlyBidInfoByProductId(paramMap);
        return bidInfoList;
    }
}
