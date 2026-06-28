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

    @PostMapping("/{id}/toggle-required")
    fun toggleRequired(@PathVariable id: Long): String {
        adminConditionService.toggleRequired(id)
        return "redirect:/admin/conditions"
    }

    @PostMapping("/{id}/toggle-active")
    fun toggleActive(@PathVariable id: Long): String {
        adminConditionService.toggleActive(id)
        return "redirect:/admin/conditions"
    }
}
