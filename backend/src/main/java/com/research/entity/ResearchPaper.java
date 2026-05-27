package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("research_paper")
public class ResearchPaper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    /** PAPER-论文 / MONOGRAPH-专著 */
    private String type;
    private Long authorId;
    private String coAuthors;
    private Long projectId;
    private String journalName;
    private String publishLevel;
    private String doi;
    private String issn;
    private LocalDate publishDate;
    private String volume;
    private String pages;
    private String abstractText;
    private String keywords;
    private String attachmentUrl;
    /** PENDING / APPROVED / REJECTED */
    private String status;
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
    private String authorName;
    @TableField(exist = false)
    private String projectName;
}
