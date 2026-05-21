package com.example.shixun7.controller;

import com.example.shixun7.entity.Cart;
import com.example.shixun7.entity.ResponseResult;
import com.example.shixun7.entity.User;
import com.example.shixun7.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private Long getUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user.getId();
        }
        // 未登录时返回 null，让前端处理
        return null;
    }

    @GetMapping
    public ResponseResult getCart(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        return ResponseResult.success(cartService.getCart(userId));
    }

    @GetMapping("/count")
    public ResponseResult getCount(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseResult.success(new HashMap<String, Integer>() {{ put("count", 0); }});
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("count", cartService.getCartCount(userId));
        return ResponseResult.success(result);
    }

    @PostMapping("/add")
    public ResponseResult addToCart(@RequestBody Map<String, Object> params, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setDessertId(Long.valueOf(params.get("dessertId").toString()));
        cart.setQuantity(Integer.parseInt(params.get("quantity").toString()));
        cartService.addToCart(cart);
        return ResponseResult.success();
    }

    @PutMapping("/{id}")
    public ResponseResult updateQuantity(@PathVariable Long id, @RequestParam Integer quantity, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        cartService.updateQuantity(id, userId, quantity);
        return ResponseResult.success();
    }

    @DeleteMapping("/{id}")
    public ResponseResult removeFromCart(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        cartService.removeFromCart(id, userId);
        return ResponseResult.success();
    }

    @DeleteMapping("/clear")
    public ResponseResult clearCart(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        cartService.clearCart(userId);
        return ResponseResult.success();
    }
}