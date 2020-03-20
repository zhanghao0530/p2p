package com.bjpowernode.p2p.timer;/**
 * ClassName:TimerManager
 * Package:com.bjpowernode.p2p.timer
 * Description:
 *
 * @date:2020/3/19 18:19
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 作者：章昊
 * 2020/3/19
 */

@Component  //将此类交给spring容器进行管理
@Slf4j
public class TimerManager {

    @Reference(interfaceClass = IncomeRecordService.class,version = "1.0.0",check = false)
    private IncomeRecordService incomeRecordService;

    //@Scheduled(cron="0/5 * * * * ?")
    public void  generateIncomePlan(){
        log.info("----------生成收益开始----------");

        incomeRecordService.generateIncomePlan(1);


        log.info("----------生成收益结束----------");
    }

    @Scheduled(cron="0/5 * * * * ?")
    public void generateIncomeBack(){
        log.info("----------收益返还开始----------");


        incomeRecordService.generateIncomeBack();

        log.info("----------收益返还结束----------");
    }

}
