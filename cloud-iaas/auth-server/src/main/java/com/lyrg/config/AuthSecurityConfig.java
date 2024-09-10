package com.lyrg.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyrg.bean.LoginResult;
import com.lyrg.model.Result;
import com.lyrg.constant.AuthConstants;
import com.lyrg.constant.BussinessEnum;
import com.lyrg.constant.HttpConstants;
import com.lyrg.impl.UserDetailsServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.UUID;

/**
 * Author: lyrg
 */
@Configuration
public class AuthSecurityConfig {
    @Resource
    private UserDetailsServiceImpl userDetailsService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form
                        .loginProcessingUrl(AuthConstants.LOGIN_URL)
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler()))
                .logout(logout -> logout
                        .logoutUrl(AuthConstants.LOGOUT_URL)
                        .logoutSuccessHandler(logoutSuccessHandler()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 登录成功处理逻辑
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // 设置响应头
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);
            // UUID当作token
            String token = UUID.randomUUID().toString();
            // 从security中获取用户信息(Security)并转换为json字符串
            String userJsonStr = JSONObject.toJSONString(authentication.getPrincipal());
            // 将token当作key，认证用户对象的json字符串当作value存入redis
            stringRedisTemplate.opsForValue().set(AuthConstants.LOGIN_TOKEN_PREFIX + token,
                    userJsonStr, Duration.ofSeconds(AuthConstants.TOKEN_TIME));
            // 封装一个登录统一结果对象
            LoginResult loginResult = new LoginResult(token, AuthConstants.TOKEN_TIME);
            Result<Object> result = Result.success(loginResult);
            // 创建一个响应结果对象
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }

    // 登录失败处理逻辑
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            // 设置响应头
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);
            // 创建统一响应结果对象
            Result<Object> result = new Result<>();
            result.setCode(BussinessEnum.OPERATION_FAIL.getCode());
            if(exception instanceof BadCredentialsException){
                result.setMsg("用户名或密码错误");
            }else if(exception instanceof UsernameNotFoundException){
                result.setMsg("用户不存在");
            } else if(exception instanceof AccountExpiredException){
                result.setMsg("账号异常，请联系管理员");
            } else if (exception instanceof InternalAuthenticationServiceException) {
                result.setMsg(exception.getMessage());
            } else {
                result.setMsg(BussinessEnum.OPERATION_FAIL.getDesc());
            }
            // 返回结果
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
        };
    }

    // 登出成功处理逻辑
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            // 设置响应头
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);
            // 从请求头中获取token
            String authorization = request.getHeader(AuthConstants.AUTHORIZATION);
            String token = authorization.replaceFirst(AuthConstants.BEARER,"");
            // 从redis中删除token
            stringRedisTemplate.delete(AuthConstants.LOGIN_TOKEN_PREFIX + token);
            // 创建一个响应结果对象
            Result<Object> result = Result.success(null);
            // 返回结果
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}