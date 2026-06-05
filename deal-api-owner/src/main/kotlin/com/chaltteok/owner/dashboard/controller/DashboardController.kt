package com.chaltteok.owner.dashboard.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.dashboard.enums.DashboardPeriod
import com.chaltteok.owner.dashboard.service.DashboardService
import jakarta.validation.constraints.PastOrPresent
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/owner/dashboard")
@Validated
class DashboardController(private val dashboardService: DashboardService) {

    @GetMapping("/overview")
    fun getOverview(
        @RequestParam(defaultValue = "DAILY") period: DashboardPeriod,
        @PastOrPresent @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @PastOrPresent @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?,
    ) = ResponseDTO.success(dashboardService.getOverview(period, from, to))

    @GetMapping("/sales-trend")
    fun getSalesTrend(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
    ) = ResponseDTO.success(dashboardService.getSalesTrend(from, to))

    @GetMapping("/top-products")
    fun getTopProducts(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
        @RequestParam(defaultValue = "10") limit: Int,
    ) = ResponseDTO.success(dashboardService.getTopProducts(from, to, limit))

    @GetMapping("/hourly-sales")
    fun getHourlySales(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
    ) = ResponseDTO.success(dashboardService.getHourlySales(date))
}
