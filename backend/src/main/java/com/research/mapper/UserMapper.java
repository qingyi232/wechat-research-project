package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.research.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT u.*, c.college_name FROM sys_user u LEFT JOIN sys_college c ON u.college_id = c.id WHERE u.id = #{id} AND u.deleted = 0")
    User selectUserWithCollege(@Param("id") Long id);

    IPage<User> selectUserPage(Page<User> page, @Param("keyword") String keyword, @Param("role") String role, @Param("collegeId") Long collegeId, @Param("status") Integer status);
}
