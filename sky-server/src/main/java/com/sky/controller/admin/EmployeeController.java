package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();


        return Result.success(employeeLoginVO);
    }

    //新增员工
    @PostMapping("")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        //因为新增员工不用返回数据，所以返回值类型是Result
        //要加上@RequestBody注解，因为前端传过来的是json格式的数据，要把它转换成EmployeeDTO对象
        employeeService.save(employeeDTO);
        return Result.success();
    }


    //分页查询员工
    @GetMapping("/page")
    public Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        //因为分页查询要返回分页结果，所以返回值类型是PageResult,但是还要加上Result来包装一下，所以最终的返回值类型是Result<PageResult>
        //不需要加上@RequestBody注解，因为前端传过来的是查询参数，直接封装成EmployeePageQueryDTO对象就行了
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    //启用禁用员工账号
    @PostMapping("status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        //因为启用禁用员工账号不用返回数据，所以返回值类型是Result
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    //根据id来查员工
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        //id是从路径中获取的，所以要加上@PathVariable注解
        //因为根据id来查员工要返回员工信息，所以返回值类型是Employee,但是还要加上Result来包装一下，所以最终的返回值类型是Result<Employee>
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    //编辑员工信息
    @PutMapping("")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        //因为编辑员工信息不用返回数据，所以返回值类型是Result
        //不需要加上@RequestBody注解，因为前端传过来的是查询参数，直接封装成EmployeeDTO对象就行了
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
