package com.lyrg.strategy;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Author: lyrg
 */
public interface LoginStrategy {
    UserDetails realLogin(String username);
}
