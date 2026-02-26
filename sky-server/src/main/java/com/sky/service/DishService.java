package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    //新增菜品和口味
    void saveWithFlavor(DishDTO dishDTO);

    //菜品分页查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //批量删除菜品
    void deleteBatch(List<Long> ids);

    //根据id查询菜品信息
    DishVO getByIdWithFlavor(Long id);

    //改变菜品状态
    void startOrStop(Integer status, Long id);

    //更新菜品信息和口味信息
    void update(DishDTO dishDTO);
}
