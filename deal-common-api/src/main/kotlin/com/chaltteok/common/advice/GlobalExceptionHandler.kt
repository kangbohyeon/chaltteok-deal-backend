package com.chaltteok.common.advice

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.common.dto.ValidationErrorDTO
import com.chaltteok.common.enums.GlobalErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException):
            ResponseEntity<ResponseDTO<List<ValidationErrorDTO>>> {
        val errorList = exception.bindingResult.fieldErrors.map { fieldError ->
            ValidationErrorDTO(
                fieldError.field, fieldError.rejectedValue?.toString(),
                reason = fieldError.defaultMessage
            )
        }

        logger.debug { "error list : $errorList" }

        return ResponseEntity.status(GlobalErrorCode.INVALID_INPUT_VALUE.status)
            .body(ResponseDTO.error(errorCode = GlobalErrorCode.INVALID_INPUT_VALUE, data = errorList))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonErrors(e: HttpMessageNotReadableException): ResponseEntity<ResponseDTO<Any>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.error(
                errorCode = GlobalErrorCode.INVALID_INPUT_VALUE,
                customMessage = "The request format is invalid"
            ))
    }
}