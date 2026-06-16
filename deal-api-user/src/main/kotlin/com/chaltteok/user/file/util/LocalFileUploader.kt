package com.chaltteok.user.file.util

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.user.file.enums.FileErrorCode
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Component
class LocalFileUploader(
    @Value("\${file.upload-dir}") private val uploadDir: String,
) {
    private val tika = Tika()
    private val allowedMimeTypes = setOf("image/jpeg", "image/png")
    private val mimeToExt = mapOf("image/jpeg" to "jpg", "image/png" to "png")

    fun upload(file: MultipartFile): Pair<String, String> {
        if (file.isEmpty) throw BusinessException(FileErrorCode.FILE_EMPTY)

        val mimeType = file.inputStream.use { tika.detect(it) }
        if (mimeType !in allowedMimeTypes) throw BusinessException(FileErrorCode.INVALID_FILE_TYPE)

        val directory = Paths.get(uploadDir).toAbsolutePath().normalize()
        if (!Files.exists(directory)) Files.createDirectories(directory)

        val originalFilename = file.originalFilename?.ifBlank { null } ?: "upload"
        val extension = mimeToExt[mimeType]!!  // MIME 기반 확장자, 원본 미사용
        val savedName = "${UUID.randomUUID()}.$extension"

        val filePath = directory.resolve(savedName).normalize()
        check(filePath.startsWith(directory)) { "경로 이탈 감지" }

        file.transferTo(filePath.toFile())
        return Pair("/images/$savedName", originalFilename)
    }
}
