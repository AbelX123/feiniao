package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("cases")
public class CaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "case_id")
    private String caseId;

    @TableField(value = "creator_id")
    private String creatorId;

    @TableField(value = "case_title")
    private String caseTitle;

    @TableField(value = "cover_url")
    private String coverUrl;

    @TableField(value = "cover_url_expiry")
    private LocalDateTime coverUrlExpiry;

    @TableField(value = "video_url")
    private String videoUrl;

    @TableField(value = "video_url_expiry")
    private LocalDateTime videoUrlExpiry;

    @TableField(value = "status")
    private String status;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
