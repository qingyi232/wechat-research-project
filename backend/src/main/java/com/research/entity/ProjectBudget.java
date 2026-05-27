package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("project_budget")
public class ProjectBudget {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private BigDecimal totalAmount;
    private BigDecimal equipmentFee;
    private BigDecimal materialFee;
    private BigDecimal travelFee;
    private BigDecimal meetingFee;
    private BigDecimal laborFee;
    private BigDecimal consultFee;
    private BigDecimal otherFee;
    private String remark;
    /** DRAFT / PENDING / APPROVED / SEALED / REJECTED */
    private String status;
    private Long reviewerId;
    private String reviewComment;
    private LocalDateTime reviewTime;
    private Long sealerId;
    private LocalDateTime sealTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String projectName;
    @TableField(exist = false)
    private String leaderName;
}
