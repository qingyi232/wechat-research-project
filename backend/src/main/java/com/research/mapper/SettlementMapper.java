package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.research.entity.ProjectSettlement;
import org.apache.ibatis.annotations.Param;

public interface SettlementMapper extends BaseMapper<ProjectSettlement> {
    IPage<ProjectSettlement> selectSettlementPage(Page<ProjectSettlement> page,
                                                   @Param("keyword") String keyword,
                                                   @Param("status") String status,
                                                   @Param("leaderId") Long leaderId);
}
