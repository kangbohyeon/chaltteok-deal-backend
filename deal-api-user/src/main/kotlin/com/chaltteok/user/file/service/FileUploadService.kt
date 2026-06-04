package com.chaltteok.user.file.service

import com.chaltteok.user.file.dto.FileUploadResponse
import org.springframework.web.multipart.MultipartFile

interface FileUploadService {
    fun upload(file: MultipartFile): FileUploadResponse
}
