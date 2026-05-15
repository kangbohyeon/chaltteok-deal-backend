package com.chaltteok.owner.popup.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.popup.dto.PopupRequest
import com.chaltteok.owner.popup.dto.PopupResponse
import com.chaltteok.owner.popup.service.OwnerPopupService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/popups")
class PopupController(private val ownerPopupService: OwnerPopupService) {

    @GetMapping
    fun getAll(): ResponseDTO<List<PopupResponse>> =
        ResponseDTO.success(ownerPopupService.getAll())

    @PostMapping
    fun create(@Valid @RequestBody request: PopupRequest): ResponseEntity<ResponseDTO<Any>> {
        ownerPopupService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }

    @PutMapping("/{popupUuid}")
    fun update(
        @PathVariable popupUuid: String,
        @Valid @RequestBody request: PopupRequest,
    ): ResponseDTO<Any> {
        ownerPopupService.update(popupUuid, request)
        return ResponseDTO.success()
    }

    @DeleteMapping("/{popupUuid}")
    fun delete(@PathVariable popupUuid: String): ResponseDTO<Any> {
        ownerPopupService.delete(popupUuid)
        return ResponseDTO.success()
    }
}
