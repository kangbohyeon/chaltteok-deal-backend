package com.chaltteok.admin.condition.controller

import com.chaltteok.admin.condition.service.AdminConditionService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/conditions")
class AdminConditionController(
    private val adminConditionService: AdminConditionService,
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("conditions", adminConditionService.findAll())
        return "condition/list"
    }

    @PostMapping("/{conditionUuid}/toggle-required")
    fun toggleRequired(@PathVariable conditionUuid: String): String {
        adminConditionService.toggleRequired(conditionUuid)
        return "redirect:/admin/conditions"
    }

    @PostMapping("/{conditionUuid}/toggle-active")
    fun toggleActive(@PathVariable conditionUuid: String): String {
        adminConditionService.toggleActive(conditionUuid)
        return "redirect:/admin/conditions"
    }
}
