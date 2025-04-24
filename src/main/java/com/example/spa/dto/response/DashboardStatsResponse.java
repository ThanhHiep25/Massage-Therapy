package com.example.spa.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private Map<String, Long> appointmentStatusCounts;
    private List<TopServiceResponse> topServices;
    private Map<String, Long> staffPerformance;
    private long todayAppointments;
}