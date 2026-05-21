package com.example.shixun7.mapper;

import com.example.shixun7.entity.Order;
import com.example.shixun7.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order`(order_no, user_id, total_amount, discount_amount, actual_amount, " +
            "address, receiver_name, receiver_phone, remark, status) " +
            "VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{discountAmount}, #{actualAmount}, " +
            "#{address}, #{receiverName}, #{receiverPhone}, #{remark}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Insert("INSERT INTO order_item(order_id, dessert_id, dessert_name, dessert_image, price, quantity, subtotal) " +
            "VALUES(#{orderId}, #{dessertId}, #{dessertName}, #{dessertImage}, #{price}, #{quantity}, #{subtotal})")
    int insertItem(OrderItem item);

    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> selectByUserId(Long userId);

    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order selectById(Long id);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectItemsByOrderId(Long orderId);

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE `order` SET status = 2, pay_time = NOW() WHERE id = #{id}")
    int pay(Long id);
}