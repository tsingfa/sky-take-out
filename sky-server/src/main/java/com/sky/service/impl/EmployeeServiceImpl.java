package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.BCryptUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对，使用BCrypt加密
        if (!BCryptUtil.checkpw(password,employee.getPassword())) {//参数1为明文密码，参数2为加密后的密码
            //如果比对失败，则密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    //新增员工
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //将DTO转成实体类
        Employee employee =new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);//对象属性拷贝
        // 补上未填充的属性
        employee.setStatus(StatusConstant.ENABLE);//默认为 1，状态正常
        //初始为默认密码
        employee.setPassword(BCryptUtil.hashpw(PasswordConstant.DEFAULT_PASSWORD,BCryptUtil.gensalt()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 账号创建/更新人，需要改为当前登录用户的id
        // 通过jwt获取当前登录id，通过ThreadLocal由 拦截器 传递给 Service实现类
        // 每一个请求都是一个单独的线程，同一线程共享线程的局部变量的值
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    //分页查询
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 1.设置页码、页面大小（底层使用ThreadLocal存储、传递参数）
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        // 2.执行查询，通过AOP拦截SQL语句，在查询语句后动态拼上limit分页逻辑
        // 原SQL对应的所有页（符合条件）的数据，经过PageHelper处理后，结果为一些页参数以及目标页的数据
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);//Page继承了List
        //3.响应结果，封装到PageResult对象
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
    * @Description: 启用/禁用员工（修改status）
    * @Param: Integer status, Long id
    */
    @Override
    public void setStatus(Integer status, Long id) {
        Employee employee = employeeMapper.getById(id);
        if (employee == null) {//账号不存在，无法更新
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        Employee updatedEmployee = Employee.builder().status(status).id(id).build();
        employeeMapper.update(updatedEmployee);//动态更新
    }

    /**
    * @Description: 查询指定id员工的信息
    * @Param: Long id
    * @return: Employee employee
    */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    // 编辑员工信息
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.getById(employeeDTO.getId());
        if (employee == null) {//账号不存在，无法更新
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        Employee updatedEmployee = new Employee();
        BeanUtils.copyProperties(employeeDTO,updatedEmployee);
        updatedEmployee.setUpdateTime(LocalDateTime.now());
        updatedEmployee.setUpdateUser(BaseContext.getCurrentId());//利用ThreadLocal传递操作者id
        employeeMapper.update(updatedEmployee);
    }

}
