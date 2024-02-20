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
     * 员工登录
     *
     * @param employeeLoginDTO
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);
        // 1.登录
        Employee employee = employeeService.login(employeeLoginDTO);
        // 2.登录成功，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),  //用于加密得到signature
                jwtProperties.getAdminTtl(),
                claims);
        // 3.组装成前端所需的结构对象（VO）
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 员工退出
     *
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    // 新增员工（需由管理员或老员工操作）
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工:{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
    * @Description: 员工的分页查询
    * @Param: EmployeePageQueryDTO
    * @return: Result<PageResult>
    */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为:{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
    * @Description: 启用/禁用员工（修改status字段）。
    * @Param: Integer status,Long id
    * @return: 
    */
    @PostMapping("/status/{status}")    //1为启用，2为禁用
    public Result setStatus(@PathVariable Integer status,Long id){
        log.info("启用/禁用id为{}的员工账号,更新status为:{}",id,status);
        employeeService.setStatus(status,id);
        return Result.success();
    }

    /**
    * @Description: 查询指定id员工的信息
    * @Param: Long id
    */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("查询id为{}的员工信息",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
    * @Description: 编辑修改员工信息
    * @Param: 
    * @return: 
    */
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息:{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

}
