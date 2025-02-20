package com.fortest.orderdelivery.app.global.security;

import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // userId를 가져오는 메서드 (컨트롤러에서 편리하게 사용)
    public Long getUserId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RoleType roleType = user.getRoleType();
        String authority = roleType.getName(); // DB에서 저장된 값

        //ROLE_ 접두사 처리 (팀원과 협의 필요)
        String roleWithPrefix = authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleWithPrefix);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    } //컨트롤러에서 userDetails.getAuthorities()를 호출하면, 유저의 권한을 가져올 수 있음
    // DB에 저장된 CUSTOMER, OWNER 같은 값을 ROLE_CUSTOMER, ROLE_OWNER처럼 변환하여 Spring Security에서 사용할 수 있도록 함.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 추가: 디버깅을 위한 toString() 메서드
    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "username='" + user.getUsername() + '\'' +
                ", role='" + user.getRoleType().getName() + '\'' +
                '}';
    }
}
