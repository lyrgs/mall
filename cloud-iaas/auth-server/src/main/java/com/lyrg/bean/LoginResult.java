package com.lyrg.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: lyrg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "登录结果")
public class LoginResult {
    @ApiModelProperty(value = "登录令牌")
    private String accessToken;
    @ApiModelProperty(value = "有效时长")
    private Long expiresIn;
}