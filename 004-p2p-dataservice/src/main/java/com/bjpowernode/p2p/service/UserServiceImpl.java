package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;

/**
 * 作者：章昊
 * 2020/3/13
 */
@Component
@Service(interfaceClass = UserService.class,version = "1.0.0",timeout = 15000)
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long queryAllUserCount() {
        redisTemplate.setStringSerializer(new StringRedisSerializer());
        Long allUserCount= (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
        if(!ObjectUtils.allNotNull(allUserCount)){
           synchronized (this){
               allUserCount= (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
               if (!ObjectUtils.allNotNull(allUserCount)){
                   allUserCount=userMapper.selectAllUserCount();
                   redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT, allUserCount);
               }
           }

        }

        return allUserCount;
    }
}
