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
    private val allowedMimeTypes = listOf("image/jpeg", "image/png")

    fun upload(file: MultipartFile): Pair<String, String> {  // (fileUrl, originalFilename)
        if (file.isEmpty) throw BusinessException(FileErrorCode.FILE_EMPTY)
        val mimeType = file.inputStream.use { tika.detect(it) }
        if (mimeType !in allowedMimeTypes) throw BusinessException(FileErrorCode.INVALID_FILE_TYPE)

        val directory = Paths.get(uploadDir).toAbsolutePath().normalize()
        if (!Files.exists(directory)) Files.createDirectories(directory)

        val originalFilename = file.originalFilename ?: "unknown.jpg"
        val extension = originalFilename.substringAfterLast(".", "jpg")
        val savedName = "${UUID.randomUUID()}.$extension"
        file.transferTo(directory.resolve(savedName).toFile())
        return Pair("/images/$savedName", originalFilename)
    }
}
