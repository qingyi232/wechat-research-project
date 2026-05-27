package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.research.entity.ResearchPaper;
import org.apache.ibatis.annotations.Param;

public interface PaperMapper extends BaseMapper<ResearchPaper> {
    IPage<ResearchPaper> selectPaperPage(Page<ResearchPaper> page,
                                         @Param("keyword") String keyword,
                                         @Param("type") String type,
                                         @Param("status") String status,
                                         @Param("authorId") Long authorId);
}
