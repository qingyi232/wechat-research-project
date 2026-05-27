package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("project_task_book")
public class TaskBook {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String objectives;
    private String researchContent;
    private String expectedResults;
    private String schedule;
    private String attachmentUrl;
    /** DRAFT / PENDING / APPROVED / REJECTED */
    private String status;
    private Long reviewerId;
    private String reviewComment;
    private LocalDateTime reviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String projectName;
}
