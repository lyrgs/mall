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

    // 登录URL
    String LOGIN_URL = "/doLogin";
    // 登出URL
    String LOGOUT_URL = "/doLogout";
    // 登录类型
    String LOGIN_TYPE = "loginType";
    // 后台用户
    String SYS_USER_LOGIN = "sysUserLogin";
    // 购物用户
    String MEMBER_LOGIN = "memberLogin";
    // token时长，7天
    Long TOKEN_TIME = 60 * 60 * 24 * 7L;
}
