package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    //新增菜品
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    //菜品分页查询
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    //删除菜品
    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    public Result delete (@RequestParam List<Long> ids) {

        dishService.deleteBatch(ids);

        return Result.success();
    }

    //根据id来查询菜品信息
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品信息")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    //改变菜品状态
    @PostMapping("/status/{status}")
    @ApiOperation("改变菜品状态")
    public Result StartOrStop(@PathVariable Integer status, Long id) {
        log.info("改变菜品状态：{},{}",status,id);
        dishService.startOrStop(status,id);
        return Result.success();
    }

    //更新菜品信息
    @PutMapping()
    @ApiOperation("更新菜品信息")
    public Result update(@RequestBody DishDTO dishDTO) {

        dishService.update(dishDTO);
        return Result.success();
    }
}