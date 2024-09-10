package com.lyrg.strategy.impl;

import com.lyrg.constant.AuthConstants;
import com.lyrg.strategy.LoginStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Author: lyrg
 */
@Service(AuthConstants.MEMBER_LOGIN)
public class MemberLoginStrategy implements LoginStrategy {
    @Override
    public UserDetails realLogin(String username) {
        return null;
    }
}