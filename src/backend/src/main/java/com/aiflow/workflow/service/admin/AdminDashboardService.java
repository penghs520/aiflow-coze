package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.dto.admin.DashboardResponse;
import com.aiflow.workflow.repository.TaskRepository;
import com.aiflow.workflow.repository.UserRepository;
import com.aiflow.workflow.repository.WorkflowRepository;
import com.aiflow.workflow.repository.admin.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;
    private final OperationLogRepository operationLogRepository;

    public DashboardResponse getDashboardData() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(1);

        long totalTasks = taskRepository.count();
        long runningTasks = taskRepository.findByStatus(2).size();
        long completedTasks = taskRepository.findByStatus(3).size();
        long failedTasks = taskRepository.findByStatus(4).size();

        long totalWorkflows = workflowRepository.count();
        long publishedWorkflows = workflowRepository.findByStatus(1).size();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalTasks(totalTasks)
                .runningTasks(runningTasks)
                .completedTasks(completedTasks)
                .failedTasks(failedTasks)
                .totalRevenue(BigDecimal.ZERO)
                .todayRevenue(BigDecimal.ZERO)
                .totalWorkflows(totalWorkflows)
                .publishedWorkflows(publishedWorkflows)
                .build();
    }
}
