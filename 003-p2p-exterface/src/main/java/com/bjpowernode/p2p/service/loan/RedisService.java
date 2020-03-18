package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:RedisService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/3/17 21:24
 * @author:zh
 */

public interface RedisService {
    /**
     * 将key对应的value放到redis中
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * 获取key对应value值
     * @param key
     * @return
     */
    String get(String key);
}
