package com.medizine.model;


import com.medizine.model.entity.User;

public class Member {
    private String memberType;
    private User user;

    public Member(String memberType, User user) {
        this.memberType = memberType;
        this.user = user;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
