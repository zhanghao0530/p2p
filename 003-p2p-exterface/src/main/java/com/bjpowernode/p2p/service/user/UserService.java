package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;

public interface UserService {
    /**
     * 获取平台注册总人数
     * @return 总人数
     */
    Long queryAllUserCount();

    /**
     * 通过手机号码查找user
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 注册操作
     * @param phone
     * @param loginPassword
     */
    User register(String phone, String loginPassword) throws Exception;

    /**
     * 根据用户标识获取用户信息
     * @return
     */
    int modifyUserById(User updateUser);
}
