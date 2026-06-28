package com.chaltteok.admin.owner.controller

import com.chaltteok.admin.owner.service.AdminOwnerService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/owners")
class AdminOwnerController(
    private val adminOwnerService: AdminOwnerService,
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        model: Model,
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        model.addAttribute("owners", adminOwnerService.findAll(pageable))
        model.addAttribute("currentPage", page)
        return "owner/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("owner", adminOwnerService.findById(id))
        return "owner/detail"
    }
}
