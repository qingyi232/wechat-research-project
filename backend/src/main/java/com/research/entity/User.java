package com.research.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String realName;
    /** TEACHER / COLLEGE_ADMIN / SCHOOL_ADMIN / FINANCE_ADMIN / SYSTEM_ADMIN */
    private String role;
    private Long collegeId;
    private String phone;
    private String email;
    private String title;
    private String avatar;
    /** 0-待审核 1-正常 2-禁用 */
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String collegeName;
    @TableField(exist = false)
    private String token;
}
