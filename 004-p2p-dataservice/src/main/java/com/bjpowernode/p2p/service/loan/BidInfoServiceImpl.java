package com.bjpowernode.p2p.service.loan;/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service
 * Description:
 *
 * @date:2020/3/14 10:28
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 作者：章昊
 * 2020/3/14
 */
@Component
@Service(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double queryAllBidMoney() {

        Double allBidMoney= (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

        if(!ObjectUtils.allNotNull(allBidMoney)){

            //开启同步代码块
            synchronized (this){

                allBidMoney= (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

                if(!ObjectUtils.allNotNull(allBidMoney)){

                    allBidMoney=bidInfoMapper.selectAllBidMoney();

                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY, allBidMoney,15, TimeUnit.MINUTES);
                }
            }
        }


        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryRecentlyBidInfoByProductId(Map<String, Object> paramMap) {
        List<BidInfo> bidInfoList=bidInfoMapper.selectRecentlyBidInfoByProductId(paramMap);
        return bidInfoList;
    }

    @Transactional
    @Override
    public void invest(Map<String, Object> paramMap) throws Exception {
        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId= (Integer) paramMap.get("loanId");
        Double bidMoney= (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");

        //超卖:实现销售的数量超过库存数量
        //使用数据库乐观锁解决超卖现象
        LoanInfo loanInfo=loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version", loanInfo.getVersion());

        //更新产品剩余可投金额

        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoney(paramMap);
        if(updateLeftProductMoneyCount<=0){
            throw new Exception("更新产品剩余可投金额失败");
        }
        //更新帐户可用余额
        int updateFinanceAccountCount=financeAccountMapper.updateFinanceAccountByBid(paramMap);
        if(updateFinanceAccountCount<=0){
            throw new Exception("更新账户可用余额失败");
        }

        //新增投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setUid(uid);
        bidInfo.setLoanId(loanId);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(new Date());
        bidInfo.setBidStatus(1);
        int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);

        if (insertBidInfoCount <= 0) {
            throw new Exception("新增投资记录失败");
        }

        //再次查询产品详情
        LoanInfo loanInfoDetail=loanInfoMapper.selectByPrimaryKey(loanId);
        //判断产品是否满标
        if(0==loanInfoDetail.getLeftProductMoney()){
            //产品满标：更新产品的状态为1和满标时间
            LoanInfo UpdateloanInfo = new LoanInfo();
            UpdateloanInfo.setId(loanId);
            UpdateloanInfo.setProductStatus(1);
            UpdateloanInfo.setProductFullTime(new Date());
            int i =loanInfoMapper.updateByPrimaryKeySelective(UpdateloanInfo);
            if(i<=0){
                throw new Exception("更新产品状态失败");
            }
        }

        //将用户投资的信息保存到redis缓存中
        redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP, phone, bidMoney);


    }

    @Override
    public List<BidUser> queryBidUserTop() {
        List<BidUser>bidUserList=new ArrayList<>();
        //从redis中获取投资排行榜
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = set.iterator();
        while(iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score=next.getScore();
            BidUser bidUser = new BidUser();
            bidUser.setPhone(phone);
            bidUser.setScore(score);
            bidUserList.add(bidUser);
        }
        return bidUserList;
    }
}
