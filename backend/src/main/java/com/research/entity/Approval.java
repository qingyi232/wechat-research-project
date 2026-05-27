package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_record")
public class Approval {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** PROJECT / BUDGET / SETTLEMENT / PAPER / PATENT */
    private String bizType;
    private Long bizId;
    /** APPROVE / REJECT / SEAL */
    private String action;
    private Long operatorId;
    private String comment;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String operatorName;
}
