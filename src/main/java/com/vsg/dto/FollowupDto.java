package com.vsg.dto;

import lombok.Data;

@Data
public class FollowupDto {
    private String id;
    private String studentId;
    private String note;
    private String reminderDate;
    private String channel;
    private String createdBy;
    private String createdAt;
}
