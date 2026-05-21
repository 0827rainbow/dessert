package com.example.shixun7.service;

import com.example.shixun7.entity.Dessert;
import com.example.shixun7.mapper.DessertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DessertService {

    @Autowired
    private DessertMapper dessertMapper;

    public List<Dessert> list(String name, Long catId, Integer status) {
        return dessertMapper.list(name, catId, status);
    }

    public Dessert getById(Long id) {
        return dessertMapper.selectById(id);
    }

    public boolean add(Dessert dessert) {
        return dessertMapper.insert(dessert) > 0;
    }

    public boolean update(Dessert dessert) {
        return dessertMapper.update(dessert) > 0;
    }

    public boolean delete(Long id) {
        return dessertMapper.deleteById(id) > 0;
    }

    public boolean reduceStock(Long dessertId, Integer quantity) {
        return dessertMapper.reduceStock(dessertId, quantity) > 0;
    }

    public List<Dessert> getRecommendList(int limit) {
        return dessertMapper.getRecommendList(limit);
    }
}