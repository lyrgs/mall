package com.lyrg.factory;

import com.lyrg.strategy.LoginStrategy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: lyrg
 */
@Component
public class LoginStrategyFactory {
    @Resource
    private Map<String, LoginStrategy> loginStrategyMap = new HashMap<>();

    // 根据用户登录类型获取具体登录策略
    public LoginStrategy getLoginStrategy(String loginType) {
        return loginStrategyMap.get(loginType);
    }
}