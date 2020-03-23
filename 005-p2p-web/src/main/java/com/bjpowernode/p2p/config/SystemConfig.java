package com.bjpowernode.p2p.config;/**
 * ClassName:SystemConfig
 * Package:com.bjpowernode.p2p.config
 * Description:
 *
 * @date:2020/3/23 17:04
 * @author:zh
 */

import com.bjpowernode.p2p.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 作者：章昊
 * 2020/3/23
 */
@Configuration
public class SystemConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String[] addPathPatterns={
                "/loan/**"
        };

        String[] excludePathPatterns={
                "/loan/loan",
                "/loan/loanInfo",
                "/loan/page/register",
                "/loan/checkPhone",
                "/loan/register",
                "/loan/messageCode",
                "/loan/page/login",
                "/loan/login"
        };

        registry.addInterceptor(new UserInterceptor()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }
}
