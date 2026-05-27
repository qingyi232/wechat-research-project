package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.research.entity.ProjectMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
    @Select("SELECT pm.*, u.real_name as user_name, c.college_name FROM project_member pm LEFT JOIN sys_user u ON pm.user_id = u.id LEFT JOIN sys_college c ON u.college_id = c.id WHERE pm.project_id = #{projectId}")
    List<ProjectMember> selectByProjectId(@Param("projectId") Long projectId);
}
