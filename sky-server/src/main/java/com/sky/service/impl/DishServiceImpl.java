package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /*
    //新增菜品
    @Override
    public void save(DishDTO dishDTO) {
        //1、将DTO对象转换为实体对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //2、设置一些DTO中没有的数据
        dish.setStatus(1);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());
        //3、调用Mapper层的方法将数据插入到数据库中
        dishMapper.insert(dish);
    }
     */

    //新增菜品和口味
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入1条数据
        dishMapper.insert(dish);
        //获取insert语句生成的主键值
        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();//取出口味
        if(flavors != null && flavors.size()>0) {
            //口味数据不是必须的,有可能用户没有提交过来数据,那么下面的就不是必须的
            //直接跳过
            flavors.forEach(dishFlavor -> {//forEach不能中断,如果要中断使用for循环
                //而且forEach不能删除集合中元素
                dishFlavor.setDishId(dishId);//相当于给这些口味和对应的菜品id联系起来
                //一般来讲新增一个菜品都是同一个id,故只要遍历完这个集合就行
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
