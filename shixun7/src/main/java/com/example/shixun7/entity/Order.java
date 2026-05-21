package com.example.shixun7.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_PAID = 2;
    public static final int STATUS_DELIVERING = 3;
    public static final int STATUS_COMPLETED = 4;
    public static final int STATUS_CANCELLED = 5;

    private Long id;
    private String orderNo;
    private Long userId;
    private Double totalAmount;      // 改为 Double
    private Double discountAmount;   // 改为 Double
    private Double actualAmount;     // 改为 Double
    private Integer status;
    private String address;
    private String receiverName;
    private String receiverPhone;
    private String remark;
    private Date payTime;
    private Date deliveryTime;
    private Date completeTime;
    private Date createTime;

    private List<OrderItem> items;
}