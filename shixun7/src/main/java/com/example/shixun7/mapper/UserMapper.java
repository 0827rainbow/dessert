package com.example.shixun7.mapper;

import com.example.shixun7.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Insert("INSERT INTO user(username, password, nickname, email, phone, role) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{email}, #{phone}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET last_login_time = NOW() WHERE id = #{id}")
    int updateLoginTime(Long id);

    @Update("UPDATE user SET nickname = #{nickname}, email = #{email}, phone = #{phone} WHERE id = #{id}")
    int updateProfile(User user);
}