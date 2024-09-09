package com.lyrg.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: lyrg
 */

@Getter
@AllArgsConstructor
// 业务相应状态码
public enum BussinessEnum {
    OPERATION_FAIL(-1, "操作失败"),
    SERVER_INNER_ERROR(9999, "服务器内部错误"),
    UN_AUTHORIZED(401, "未授权"),
    ACCESS_DENY_FAIL(403, "权限不足");
    private Integer code;
    private String desc;
}