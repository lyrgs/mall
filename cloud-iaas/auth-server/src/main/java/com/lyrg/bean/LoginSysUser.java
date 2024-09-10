package com.lyrg.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class LoginSysUser implements Serializable {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;

    /**
     * 状态  0：禁用   1：正常
     */
    private Integer status;

    /**
     * 用户所在的商城Id
     */
    @TableField(value = "shop_id")
    private Long shopId;

    private static final long serialVersionUID = 1L;
}
