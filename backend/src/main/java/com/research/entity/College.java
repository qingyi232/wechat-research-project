package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_college")
public class College {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String collegeName;
    private String collegeCode;
    private String description;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
