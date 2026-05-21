package com.example.shixun7.service;

import com.example.shixun7.entity.User;
import com.example.shixun7.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            userMapper.updateLoginTime(user.getId());
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return false;
        }
        return userMapper.insert(user) > 0;
    }

    public User getProfile(Long id) {
        return userMapper.selectById(id);
    }

    public boolean updateProfile(User user) {
        return userMapper.updateProfile(user) > 0;
    }
}