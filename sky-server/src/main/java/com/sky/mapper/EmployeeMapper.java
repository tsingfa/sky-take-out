package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
    * @Description: 新增员工
    * @Param: Employee 填好信息的员工
    */
    @Insert("insert into employee(name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);
    
    /**
    * @Description: 员工分页查询
    * @Param: EmployeePageQueryDTO 分页参数
    * @return: Page<Employee> 原始SQL查询的是符合过滤条件的所有页数据，
     * 但是经过PageHelper AOP拦截处理后，返回的是含有分页相关参数的目标页数据。
    */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    // 动态更新员工信息
    void update(Employee employee);

    // 查询指定id用户的信息
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
