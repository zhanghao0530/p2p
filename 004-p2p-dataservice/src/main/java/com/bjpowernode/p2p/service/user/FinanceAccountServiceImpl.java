package com.bjpowernode.p2p.service.user;/**
 * ClassName:FinanceAccountServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2020/3/18 1:00
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者：章昊
 * 2020/3/18
 */
@Component
@Service(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 15000)
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinaceAccountByUid(Integer uid) {

        return financeAccountMapper.selectfinanceAccountByUid(uid);
    }
}
