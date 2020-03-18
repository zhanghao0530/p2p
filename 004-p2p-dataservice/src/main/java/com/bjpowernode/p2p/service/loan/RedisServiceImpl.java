package com.bjpowernode.p2p.service.loan;/**
 * ClassName:RedisServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/3/17 21:26
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 作者：章昊
 * 2020/3/17
 */
@Component
@Service(interfaceClass = RedisService.class,version = "1.0.0",timeout = 15000)
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    @Override
    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key, value,60, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {

        return (String) redisTemplate.opsForValue().get(key);
    }
}
