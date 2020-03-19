package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:BidUser
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/3/18 14:45
 * @author:动力节点
 */
@Data
public class BidUser implements Serializable {

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 分数
     */
    private Double score;


}
