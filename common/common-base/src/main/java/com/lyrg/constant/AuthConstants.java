package com.lyrg.constant;

/**
 * Author: lyrg
 */
// 认证授权常量类
public interface AuthConstants {
    // 请求头中存放token值的前缀key
    String AUTHORIZATION = "Authorization";
    // token值前缀
    String BEARER = "bearer ";
    // token值在redis中的key前缀
    String LOGIN_TOKEN_PREFIX = "login_token:";
}
