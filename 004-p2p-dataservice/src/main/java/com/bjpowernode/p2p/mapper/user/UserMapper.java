package com.bjpowernode.p2p.mapper.user;


import com.bjpowernode.p2p.model.user.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    /**
     * 获取平台注册总人数
     * @return
     */
    Long selectAllUserCount();

    /**
     * 通过手机号获取user对象
     * @param phone
     * @return
     */
    User selectUserByPhone(String phone);

    /**
     * 判断用户登陆是否成功
     * @param userDetail
     * @return
     */
    User queryUserByPhoneAndLoginPassword(User userDetail);
}