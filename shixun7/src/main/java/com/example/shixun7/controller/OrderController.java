package com.example.shixun7.controller;

import com.example.shixun7.entity.Order;
import com.example.shixun7.entity.ResponseResult;
import com.example.shixun7.entity.User;
import com.example.shixun7.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null ? user.getId() : null;
    }

    @GetMapping("/list")
    public ResponseResult getUserOrders(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        System.out.println("当前登录用户ID: " + userId);
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseResult.success(orders);
    }

    @PostMapping("/createFromCart")
    public ResponseResult createFromCart(@RequestBody Map<String, String> params, HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        // 获取参数并处理 null
        String address = params.getOrDefault("address", "");
        String receiverName = params.getOrDefault("receiverName", "");
        String receiverPhone = params.getOrDefault("receiverPhone", "");
        String remark = params.getOrDefault("remark", "");

        System.out.println("创建订单 - address: " + address);
        System.out.println("创建订单 - receiverName: " + receiverName);
        System.out.println("创建订单 - receiverPhone: " + receiverPhone);

        // 验证必填字段
        if (address == null || address.trim().isEmpty()) {
            return ResponseResult.error("收货地址不能为空");
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            return ResponseResult.error("收货人姓名不能为空");
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            return ResponseResult.error("联系电话不能为空");
        }

        try {
            Order order = orderService.createOrderFromCart(
                    userId, address, receiverName, receiverPhone, remark
            );
            return ResponseResult.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    @PostMapping("/createDirect")
    public ResponseResult createDirect(@RequestBody Map<String, Object> params, HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        String address = (String) params.getOrDefault("address", "");
        String receiverName = (String) params.getOrDefault("receiverName", "");
        String receiverPhone = (String) params.getOrDefault("receiverPhone", "");
        String remark = (String) params.getOrDefault("remark", "");

        if (address == null || address.trim().isEmpty()) {
            return ResponseResult.error("收货地址不能为空");
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            return ResponseResult.error("收货人姓名不能为空");
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            return ResponseResult.error("联系电话不能为空");
        }

        try {
            Order order = orderService.createOrderDirect(
                    userId,
                    Long.valueOf(params.get("dessertId").toString()),
                    Integer.parseInt(params.get("quantity").toString()),
                    address, receiverName, receiverPhone, remark
            );
            return ResponseResult.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    @PostMapping("/pay/{orderId}")
    public ResponseResult payOrder(@PathVariable Long orderId, HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        boolean result = orderService.payOrder(orderId);
        return result ? ResponseResult.success() : ResponseResult.error("支付失败");
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseResult cancelOrder(@PathVariable Long orderId, HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        boolean result = orderService.cancelOrder(orderId);
        return result ? ResponseResult.success() : ResponseResult.error("取消失败");
    }
}