package com.pinyougou.shop.service.impl;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 *动态认证授权类
 * 配置动态认证类UserDetailServiceImpl实现认证。
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //根据登录名到数据库中查询
        TbSeller seller = sellerService.findOne(username);
        if (seller != null && "1".equals(seller.getStatus())){

            return new User(username,seller.getPassword(),authorities);
        }
        return null;
    }
    public void  setSellerService(SellerService sellerService){
        this.sellerService =sellerService;
    }
}
