package com.medizine.model.entity;

import com.medizine.model.Authentication;

public class AuthUser {
    private User user;
    private Authentication authentication;

    public AuthUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
