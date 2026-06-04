package com.chaltteok.user.file.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.file.dto.FileUploadResponse
import com.chaltteok.user.file.service.FileUploadService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/user/files")
class FileUploadController(
    private val fileUploadService: FileUploadService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        authentication: Authentication,
        @RequestParam("file") file: MultipartFile,
    ): ResponseDTO<FileUploadResponse> {
        authentication.principal as Long  // 인증 강제 (미인증 시 401)
        return ResponseDTO.success(fileUploadService.upload(file))
    }
}
