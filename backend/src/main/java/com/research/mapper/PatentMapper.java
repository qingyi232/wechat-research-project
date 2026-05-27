package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.research.entity.ResearchPatent;
import org.apache.ibatis.annotations.Param;

public interface PatentMapper extends BaseMapper<ResearchPatent> {
    IPage<ResearchPatent> selectPatentPage(Page<ResearchPatent> page,
                                           @Param("keyword") String keyword,
                                           @Param("status") String status,
                                           @Param("inventorId") Long inventorId);
}
