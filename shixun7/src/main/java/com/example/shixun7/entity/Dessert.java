package com.example.shixun7.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class Dessert {

    @Schema(title = "甜点ID", example = "1")
    private Long id;

    @Schema(title = "甜点名称", example = "提拉米苏", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(title = "甜点描述", example = "意大利经典甜点，咖啡与奶酪的完美结合")
    private String descp;

    @Schema(title = "图片URL", example = "/photo/abc123.jpg")
    private String photoUrl;

    @Schema(title = "单价", example = "32.0")
    private Double price;

    @Schema(title = "原价", example = "45.0")
    private Double originalPrice;

    @Schema(title = "折扣率", example = "0.8")
    private Double discount;

    @Schema(title = "发布日期", example = "2024-03-15")
    private Date releaseDate;

    @Schema(title = "所属分类ID", example = "1")
    private Long catId;

    @Schema(title = "分类名称（联查返回）", example = "蛋糕系列")
    private String categoryName;

    @Schema(title = "库存数量", example = "50")
    private Integer stock;

    @Schema(title = "最低库存预警值", example = "5")
    private Integer minStock;

    @Schema(title = "累计销量", example = "320")
    private Integer sales;

    @Schema(title = "评分", example = "4.8")
    private Double rating;

    @Schema(title = "评价数量", example = "128")
    private Integer reviewCount;

    @Schema(title = "状态：0下架 1上架 2缺货", example = "1")
    private Integer status;

    @Schema(title = "是否热门：0否 1是", example = "0")
    private Integer isHot;

    @Schema(title = "是否推荐：0否 1是", example = "1")
    private Integer isRecommend;

    @Schema(title = "创建时间", example = "2024-02-10 10:30:00")
    private Date createTime;

    @Schema(title = "更新时间", example = "2024-03-20 14:25:00")
    private Date updateTime;
}