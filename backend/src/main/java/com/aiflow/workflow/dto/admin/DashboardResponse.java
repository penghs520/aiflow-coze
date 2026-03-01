package com.aiflow.workflow.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long totalUsers;
    private Long todayNewUsers;
    private Long activeUsers;

    private Long totalTasks;
    private Long todayTasks;
    private Long runningTasks;
    private Long completedTasks;
    private Long failedTasks;

    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;

    private Long totalWorkflows;
    private Long publishedWorkflows;
}
