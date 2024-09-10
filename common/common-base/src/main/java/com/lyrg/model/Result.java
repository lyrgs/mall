package com.lyrg.model;

import com.lyrg.constant.BussinessEnum;
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
@ApiModel(value = "统一响应结果对象")
public class Result<T> {
    @ApiModelProperty(value = "状态码")
    private Integer code = 200;
    @ApiModelProperty(value = "消息")
    private String msg = "ok";
    @ApiModelProperty(value = "数据")
    private T data;

    public static <T> Result<T> success(T data) {
        return Result.<T>builder().data(data).build();
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return Result.<T>builder().code(code).msg(msg).data(null).build();
    }

    public static <T> Result<T> fail(BussinessEnum bussinessEnum) {
        return Result.<T>builder().code(bussinessEnum.getCode()).msg(bussinessEnum.getDesc()).data(null).build();
    }
}