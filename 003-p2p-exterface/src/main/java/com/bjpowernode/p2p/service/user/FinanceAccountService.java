package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * ClassName:FinanceAccountService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2020/3/18 1:00
 * @author:zh
 */
public interface FinanceAccountService {
    FinanceAccount queryFinaceAccountByUid(Integer id);
}
