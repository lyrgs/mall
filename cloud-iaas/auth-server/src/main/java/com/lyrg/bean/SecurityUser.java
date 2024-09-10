package com.lyrg.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: lyrg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Security认识的用户类
public class SecurityUser implements UserDetails {
    // 商城后台管理系统用户的相关属性
    private Long userId;
    private String username;
    private String password;
    private Integer status;
    private Long shopId;
    private String loginType;
    private Set<String> perms = new HashSet<>();

    // 商城购物系统会员的相关属性
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.loginType + this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }

    public Set<String> getPerms() {
        HashSet<String> finalPermsSet = new HashSet<>();
        perms.forEach(perm->{
            if(perm.contains(",")){
                // 包含，即一条权限包含多条权限
                // 根据,分割
                String[] realPerms = perm.split(",");
                for(String realPerm : realPerms){
                    finalPermsSet.add(realPerm);
                }
            }else {
                // 不包含，即一条权限
                finalPermsSet.add(perm);
            }
        });
        return perms;
    }
}