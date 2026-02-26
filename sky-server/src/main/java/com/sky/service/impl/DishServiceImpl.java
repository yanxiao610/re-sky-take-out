package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        //获取分页查询参数 当前页码 每页条数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //查询数据
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        //获取分页查询结果
        long total = page.getTotal();
        List<DishVO> records = page.getResult();

        //封装分页查询结果并返回
        return new PageResult(total, records);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for(Long id : ids) { //遍历要删除的菜品id集合
            //查询当前菜品的状态是什么,如果是起售状态,那么就不能删除
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == 1) {
                //起售中的菜品不能删除,抛出一个业务异常
                throw new RuntimeException("起售中的菜品不能删除");
            }
        }

        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {

        Dish dish = dishMapper.getById(id);//查询菜品是Entity类的,要先用Dish类来接收
        List<DishFlavor> dishflavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishflavors);
        return dishVO;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);//修改菜品表基本信息

        /*
           1、先删除原有的口味数据
           2、再插入新的口味数据
         */
        dishFlavorMapper.deleteByDishId(dish.getId());//删除原有的口味数据

        //重新插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()>0){
            flavors.forEach(dishFlavor ->{
                dishFlavor.setDishId(dishDTO.getId());
            });
        }
        dishFlavorMapper.insertBatch(flavors);

    }

}
