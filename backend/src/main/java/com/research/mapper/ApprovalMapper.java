package com.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.research.entity.Approval;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface ApprovalMapper extends BaseMapper<Approval> {
    @Select("SELECT a.*, u.real_name as operator_name FROM approval_record a LEFT JOIN sys_user u ON a.operator_id = u.id WHERE a.biz_type = #{bizType} AND a.biz_id = #{bizId} ORDER BY a.create_time DESC")
    List<Approval> selectByBiz(@Param("bizType") String bizType, @Param("bizId") Long bizId);
}
