// entity/Cart.java
package com.example.shixun7.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long dessertId;
    private Integer quantity;
    private Date createTime;
    private Date updateTime;

    // 关联字段（非数据库字段，用于前端展示）
    private String dessertName;
    private String dessertPhoto;
    private Double dessertPrice;  // 改为 Double
}