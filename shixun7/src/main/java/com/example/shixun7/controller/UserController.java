package com.example.shixun7.controller;

import com.example.shixun7.entity.ResponseResult;
import com.example.shixun7.entity.User;
import com.example.shixun7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");

        System.out.println("登录请求 - 用户名: " + username);

        User user = userService.login(username, password);
        if (user != null) {
            // 保存用户信息到 session
            session.setAttribute("user", user);
            System.out.println("登录成功 - 用户ID: " + user.getId());
            System.out.println("Session ID: " + session.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("token", "token-" + System.currentTimeMillis());
            result.put("user", user);

            return ResponseResult.success(result);
        }
        System.out.println("登录失败 - 用户名或密码错误");
        return ResponseResult.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user) {
        if (userService.register(user)) {
            return ResponseResult.success();
        }
        return ResponseResult.error("用户名已存在");
    }

    @GetMapping("/profile")
    public ResponseResult getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseResult.success(userService.getProfile(user.getId()));
        }
        return ResponseResult.error("未登录");
    }

    @GetMapping("/logout")
    public ResponseResult logout(HttpSession session) {
        session.invalidate();
        return ResponseResult.success();
    }
}