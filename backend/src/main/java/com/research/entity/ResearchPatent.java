package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("research_patent")
public class ResearchPatent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String patentName;
    /** INVENTION-发明 / UTILITY-实用新型 / DESIGN-外观设计 */
    private String patentType;
    private String patentNo;
    private String applicationNo;
    private Long inventorId;
    private String coInventors;
    private Long projectId;
    private LocalDate applyDate;
    private LocalDate authorizeDate;
    /** DRAFT / PENDING_SEAL-待盖章 / SEALED-已盖章 / UNDER_REVIEW-实质审查中 / AUTHORIZED-已授权 / REJECTED-已驳回 */
    private String status;
    private String attachmentUrl;
    private Long reviewerId;
    private String reviewComment;
    private LocalDateTime reviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String inventorName;
    @TableField(exist = false)
    private String projectName;
}
