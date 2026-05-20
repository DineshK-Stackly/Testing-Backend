package com.stackly.common.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StandupDashboardDto {
    private Long userid;
    private String userName;
    private LocalDateTime submittedat;
    private List<QuestionDto> standup;
}

