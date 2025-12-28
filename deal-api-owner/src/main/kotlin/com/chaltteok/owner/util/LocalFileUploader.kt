package com.chaltteok.owner.util

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.owner.enums.OwnerErrorCode
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Component
class LocalFileUploader(
    @Value("\${file.upload-dir}")
    private val uploadDir: String
) {
    private val tika = Tika()

    fun uploadFile(file: MultipartFile): String {

        // 파일 존재 여부 확인
        if (file.isEmpty) {
            throw BusinessException(OwnerErrorCode.FILE_EMPTY)
        }

        // mime type check
        val mimeType = try {
            file.inputStream.use { inputStream ->
                tika.detect(inputStream)
            }
        } catch (e: Exception) {
            throw BusinessException(OwnerErrorCode.FILE_UPLOAD_ERROR)
        }


        // 허용 리스트 검증
        val allowedMimeTypes = listOf("image/jpeg", "image/png")

        if (mimeType !in allowedMimeTypes) {
            throw BusinessException(OwnerErrorCode.INVALID_FILE_TYPE)
        }

        val directory = Paths.get(uploadDir).toAbsolutePath().normalize()

        // 디렉토리 없으면 생성
        if (!Files.exists(directory)) {
            Files.createDirectories(directory)
        }

        // 파일명 중복 방지를 위한 UUID 생성
        val originalFilename = file.originalFilename ?: "unknown.jpg"
        val extension = originalFilename.substringAfterLast(".", "jpg")
        val savedFileName = "${UUID.randomUUID()}.$extension"

        // 파일 저장
        val filePath = directory.resolve(savedFileName)
        file.transferTo(filePath.toFile())

        // 저장된 경로 반환
        return "$uploadDir/$savedFileName"
    }
}