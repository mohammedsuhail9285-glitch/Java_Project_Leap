package com.coldchain.tracker.service;

import com.coldchain.tracker.dto.response.DashboardSummaryResponse;

/**
 * Service interface for aggregating cold-chain business KPIs and operational metrics.
 */
public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary();
}
