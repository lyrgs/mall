package com.lyrg.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyrg.model.Result;
import com.lyrg.config.WhiteUrlsConfig;
import com.lyrg.constant.AuthConstants;
import com.lyrg.constant.BussinessEnum;
import com.lyrg.constant.HttpConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import java.util.Date;

/**
 * author: lyrg
 * 全局token过滤器
 * 前后端约定好令牌存放位置：请求头的Authorization bearer token
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    @Resource
    private WhiteUrlsConfig whiteUrlsConfig;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 校验token
     * 1.获取请求路径
     * 2.判断请求路径是否可以放行
     * 放行：不需要身份验证
     * 不放行：需要身份验证
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public reactor.core.publisher.Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求路径
        String path = request.getPath().toString();
        // 判断当前请求路径是否可以放行
        if (whiteUrlsConfig.getAllowUrls().contains(path)) {
            // 放行
            return chain.filter(exchange);
        }
        // 不放行，从约定好的位置获取Authorization的值，值的格式为 bearer token
        String authorizationValue = request.getHeaders().getFirst(AuthConstants.AUTHORIZATION);
        // 判断Authorization是否存在
        if (StringUtils.hasText(authorizationValue)) {
            // 从Authorization中获取token
            String tokenValue = authorizationValue.replace(AuthConstants.BEARER, "").trim();
            // 判断token是否有值且是否在redis中存在
            if(StringUtils.hasText(tokenValue) && stringRedisTemplate.hasKey(AuthConstants.LOGIN_TOKEN_PREFIX + tokenValue)){
                // 身份验证通过，放行
                return chain.filter(exchange);
            }
        }
        // 身份验证失败
        log.error("拦截非法请求，时间：{}，请求路径API：{}",new Date(),path);
        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 设置响应头信息
        response.getHeaders().set(HttpConstants.CONTENT_TYPE,HttpConstants.APPLICATION_JSON);
        // 设置响应消息
        Result<Object> result = Result.fail(BussinessEnum.UN_AUTHORIZED);
        // 将result对象转换为json字符串
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return -5;
    }
}