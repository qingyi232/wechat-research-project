package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("research_project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectName;
    /** HORIZONTAL-横向 / VERTICAL-纵向 */
    private String projectCategory;
    /** BIDDING-招标 / COMMISSION-委托 (横向) ; COLLEGE-院级 / SCHOOL-校级 / PROVINCIAL-省级 / NATIONAL-国家级 (纵向) */
    private String projectType;
    private String projectLevel;
    private String projectSource;
    private Long leaderId;
    private Long collegeId;
    private BigDecimal fundingAmount;
    private LocalDate applyDate;
    private LocalDate approvalDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate completionDate;
    /** DRAFT-草稿 / PENDING_SEAL-待盖章 / SEALED-已盖章 / PENDING_REVIEW-待评审 / REVIEWED-已评审 /
     *  PENDING_APPROVAL-待立项 / APPROVED-已立项 / IN_PROGRESS-进行中 / PENDING_COMPLETION-待结题 / COMPLETED-已结题 / REJECTED-已驳回 */
    private String status;
    private String description;
    private String attachmentUrl;
    private String rejectReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String leaderName;
    @TableField(exist = false)
    private String collegeName;
}
