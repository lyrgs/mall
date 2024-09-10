package com.lyrg.impl;

import com.lyrg.constant.AuthConstants;
import com.lyrg.factory.LoginStrategyFactory;
import com.lyrg.strategy.LoginStrategy;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Author: lyrg
 */
// 项目自己的认证流程
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private LoginStrategyFactory loginStrategyFactory;

    private final HttpServletRequest httpServletRequest;

    public UserDetailsServiceImpl(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String loginType = request.getHeader(AuthConstants.LOGIN_TYPE);
        System.out.println("loginType========="+loginType);
        // 判断请求来自哪个系统
        if(!StringUtils.hasText(loginType)){
            throw new InternalAuthenticationServiceException("非法登录，登录类型不正确");
        }
        // 通过登录工厂获取具体登录对象
        LoginStrategy instance = loginStrategyFactory.getLoginStrategy(loginType);
        return instance.realLogin(username);
    }
}