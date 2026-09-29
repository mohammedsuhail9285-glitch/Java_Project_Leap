package com.coldchain.tracker.controller;

import com.coldchain.tracker.dto.response.DashboardSummaryResponse;
import com.coldchain.tracker.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Cold-Chain Executive and Operations Dashboard metrics.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "7. Dashboard Module", description = "Endpoints for executive summaries, compliance ratios, and operational KPIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping({"/api/v1/dashboard/summary", "/dashboard/summary"})
    @Operation(summary = "Get cold-chain dashboard summary", description = "Retrieves total shipments, active shipments, delivered count, violations count, fleet stats, and recent alerts")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
