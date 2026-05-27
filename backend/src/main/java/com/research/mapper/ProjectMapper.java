package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.research.entity.Project;
import org.apache.ibatis.annotations.Param;

public interface ProjectMapper extends BaseMapper<Project> {
    IPage<Project> selectProjectPage(Page<Project> page,
                                     @Param("keyword") String keyword,
                                     @Param("category") String category,
                                     @Param("type") String type,
                                     @Param("status") String status,
                                     @Param("leaderId") Long leaderId,
                                     @Param("collegeId") Long collegeId);

    Project selectProjectDetail(@Param("id") Long id);
}
