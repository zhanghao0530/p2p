package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVo;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 作者：章昊
 * 2020/3/13
 */
@Component
@Service(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    public Double queryHistoryAvgerageRate() {

        //将该值存放到缓存中，用户进来首先去redis缓存中查询，如果有:直接使用,如果没有:去数据库查询并存放到redis缓存中、
        //设置redis模版对象key的序列化方式为字符串序列化方式

        redisTemplate.setStringSerializer(new StringRedisSerializer());
        Double historyAverageRate= (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //判断是否有值
        if(!ObjectUtils.allNotNull(historyAverageRate)){

            synchronized (this){

                historyAverageRate= (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
                //再次判断是否为空
                if(!ObjectUtils.allNotNull(historyAverageRate)){
                        System.out.println("从数据库中查询……");
                        //说明redis数据库为空，需要在数据库取值
                        historyAverageRate=loanInfoMapper.selectHistoryAverageRate();
                        redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE, historyAverageRate,15, TimeUnit.MINUTES);
                        return historyAverageRate;

                }else {
                    System.out.println("从redis缓存中查询。。。。。");
                }
            }
        }else {
            System.out.println("从redis缓存中查询。。。。。");
        }


        return historyAverageRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        List<LoanInfo>loanInfoList=loanInfoMapper.selectLoinInfoListByProductType(paramMap);
        return loanInfoList;
    }
    //业务层设计方法的粒度要比DA0要粗,因为业务层提供的都是-个一个完整的业务功能
    //DAO往往是实现这个业务方法中的某一个步骤
    @Override
    public PaginationVo<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {
        PaginationVo paginationVo=new PaginationVo();

        Long total=loanInfoMapper.selectTotal(paramMap);

        List<LoanInfo>loanInfoList=loanInfoMapper.selectLoinInfoListByProductType(paramMap);

        paginationVo.setDataList(loanInfoList);

        paginationVo.setTotal(total);

        return paginationVo;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        LoanInfo loanInfo=loanInfoMapper.selectByPrimaryKey(id);
        return loanInfo;
    }
}
