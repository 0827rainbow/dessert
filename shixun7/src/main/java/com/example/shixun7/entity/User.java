package com.example.shixun7.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private Date lastLoginTime;
    private Date createTime;
    private Date updateTime;
}