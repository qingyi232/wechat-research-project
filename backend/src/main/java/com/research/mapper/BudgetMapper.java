package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.research.entity.ProjectBudget;
import org.apache.ibatis.annotations.Param;

public interface BudgetMapper extends BaseMapper<ProjectBudget> {
    IPage<ProjectBudget> selectBudgetPage(Page<ProjectBudget> page,
                                          @Param("keyword") String keyword,
                                          @Param("status") String status,
                                          @Param("leaderId") Long leaderId);
}
