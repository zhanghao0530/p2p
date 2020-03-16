package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 作者：章昊
 * 2020/3/13
 */
@Component
@Service(interfaceClass = UserService.class, version = "1.0.0", timeout = 15000)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public Long queryAllUserCount() {

        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

        if (!ObjectUtils.allNotNull(allUserCount)) {

            synchronized (this) {

                allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

                if (!ObjectUtils.allNotNull(allUserCount)) {

                    allUserCount = userMapper.selectAllUserCount();

                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT, allUserCount,15, TimeUnit.MINUTES);
                }
            }

        }

        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        User user=userMapper.selectUserByPhone(phone);

        return user;
    }
    @Transactional
    @Override
    public User register(String phone, String loginPassword) throws Exception {
        User user=new User();
        user.setPhone(phone);
        user.setAddTime(new Date());
        user.setLoginPassword(loginPassword);
        user.setLastLoginTime(new Date());
        int insertUserCount=userMapper.insertSelective(user);
        if(insertUserCount<=0){
            throw new Exception("新增账户失败");
        }
        //根据手机号查询用户信息
        //User userDetail =userMapper.selectUserByPhone(phone);

        //新增账户，并设置余额为888
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAvailableMoney(888.0);
        financeAccount.setUid(user.getId());
        int insertFinaceAccountCount=financeAccountMapper.insertSelective(financeAccount);
        if(insertFinaceAccountCount<=0){
            throw new Exception("开立账户失败");
        }

        return user;
    }
}
