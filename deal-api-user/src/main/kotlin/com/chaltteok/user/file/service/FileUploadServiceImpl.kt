package com.chaltteok.user.file.service

import com.chaltteok.core.domain.Attachment
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.user.file.dto.FileUploadResponse
import com.chaltteok.user.file.util.LocalFileUploader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FileUploadServiceImpl(
    private val localFileUploader: LocalFileUploader,
    private val attachmentRepository: AttachmentRepository,
) : FileUploadService {

    @Transactional
    override fun upload(file: MultipartFile): FileUploadResponse {
        val (fileUrl, originalFilename) = localFileUploader.upload(file)
        val attachment = attachmentRepository.save(
            Attachment(fileUrl = fileUrl, originalFilename = originalFilename)
        )
        return FileUploadResponse(
            attachmentUuid = attachment.attachmentUuid,
            fileUrl = fileUrl,
            originalFilename = originalFilename,
        )
    }
}
