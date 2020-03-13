package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.concurrent.TimeUnit;

/**
 * 作者：章昊
 * 2020/3/13
 */
@Component
@Service(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    public Double queryHistoryAvgerageRate() {

        //将该值存放到缓存中，用户进来首先去redis缓存中查询，如果有:直接使用,如果没有:去数据库查询并存放到redis缓存中、
        //设置redis模版对象key的序列化方式为字符串序列化方式

        redisTemplate.setStringSerializer(new StringRedisSerializer());
        Double historyAverageRate= (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //判断是否有值
        if(!ObjectUtils.allNotNull(historyAverageRate)){

            synchronized (this){

                historyAverageRate= (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
                //再次判断是否为空
                if(!ObjectUtils.allNotNull(historyAverageRate)){
                        System.out.println("从数据库中查询……");
                        //说明redis数据库为空，需要在数据库取值
                        historyAverageRate=loanInfoMapper.selectHistoryAverageRate();
                        redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE, historyAverageRate,15, TimeUnit.MINUTES);
                        return historyAverageRate;

                }else {
                    System.out.println("从redis缓存中查询。。。。。");
                }
            }
        }else {
            System.out.println("从redis缓存中查询。。。。。");
        }


        return historyAverageRate;
    }
}
