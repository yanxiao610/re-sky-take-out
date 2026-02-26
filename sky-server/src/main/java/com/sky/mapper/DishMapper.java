package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    //新增菜品
    void insert(Dish dish);

    //菜品分页查询
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //根据id来查菜品
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    //根据菜品id集合批量删除
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    //更新菜品
    void update(Dish dish);
}
