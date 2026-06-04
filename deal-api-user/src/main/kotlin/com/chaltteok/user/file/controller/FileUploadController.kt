package com.chaltteok.user.file.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.core.domain.Attachment
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.user.file.dto.FileUploadResponse
import com.chaltteok.user.file.util.LocalFileUploader
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/user/files")
class FileUploadController(
    private val localFileUploader: LocalFileUploader,
    private val attachmentRepository: AttachmentRepository,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(@RequestParam("file") file: MultipartFile): ResponseDTO<FileUploadResponse> {
        val (fileUrl, originalFilename) = localFileUploader.upload(file)
        val attachment = attachmentRepository.save(
            Attachment(fileUrl = fileUrl, originalFilename = originalFilename)
        )
        return ResponseDTO.success(
            FileUploadResponse(
                attachmentUuid = attachment.attachmentUuid,
                fileUrl = fileUrl,
                originalFilename = originalFilename,
            )
        )
    }
}
