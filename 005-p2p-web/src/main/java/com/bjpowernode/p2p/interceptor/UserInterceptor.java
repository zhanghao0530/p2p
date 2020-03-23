package com.bjpowernode.p2p.interceptor;/**
 * ClassName:UserInterceptor
 * Package:com.bjpowernode.p2p.interceptor
 * Description:
 *
 * @date:2020/3/23 17:02
 * @author:zh
 */

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.user.User;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 作者：章昊
 * 2020/3/23
 */
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if(!ObjectUtils.allNotNull(sessionUser)){
            response.sendRedirect(request.getContextPath()+"/loan/page/login");

            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
