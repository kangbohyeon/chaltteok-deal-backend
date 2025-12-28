package com.chaltteok.common.advice

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.common.dto.ValidationErrorDTO
import com.chaltteok.common.enums.GlobalErrorCode
import com.chaltteok.common.exception.BusinessException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.NoHandlerFoundException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ResponseDTO<Any>> {
        logger.warn { "Business Exception: ${e.errorCode.name} - ${e.errorCode.message}" }
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ResponseDTO.error(e.errorCode))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException):
            ResponseEntity<ResponseDTO<List<ValidationErrorDTO>>> {
        val errorList = exception.bindingResult.fieldErrors.map { fieldError ->
            ValidationErrorDTO(
                fieldError.field, fieldError.rejectedValue?.toString(),
                reason = fieldError.defaultMessage
            )
        }

        logger.warn { "error list : $errorList" }

        return ResponseEntity.status(GlobalErrorCode.INVALID_INPUT_VALUE.status)
            .body(ResponseDTO.error(errorCode = GlobalErrorCode.INVALID_INPUT_VALUE, data = errorList))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonErrors(e: HttpMessageNotReadableException): ResponseEntity<ResponseDTO<Any>> {
        logger.warn { "JSON Parsing Error: ${e.message}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ResponseDTO.error(
                    errorCode = GlobalErrorCode.INVALID_INPUT_VALUE,
                    customMessage = "The request format is invalid"
                )
            )
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceeded(e: MaxUploadSizeExceededException): ResponseEntity<ResponseDTO<Any>> {
        logger.warn { "FILE_TOO_LARGE : ${e.message}" }
        return ResponseEntity
            .status(GlobalErrorCode.FILE_TOO_LARGE.status)
            .body(ResponseDTO.error(GlobalErrorCode.FILE_TOO_LARGE))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ResponseDTO<Any>> {
        logger.warn { "Type Mismatch: ${e.propertyName} required ${e.requiredType?.simpleName}" }
        return ResponseEntity
            .status(GlobalErrorCode.INVALID_TYPE_VALUE.status)
            .body(ResponseDTO.error(GlobalErrorCode.INVALID_TYPE_VALUE))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(e: HttpRequestMethodNotSupportedException): ResponseEntity<ResponseDTO<Any>> {
        logger.warn { "Method Not Supported: ${e.method}" }
        return ResponseEntity
            .status(GlobalErrorCode.METHOD_NOT_ALLOWED.status)
            .body(ResponseDTO.error(GlobalErrorCode.METHOD_NOT_ALLOWED))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<ResponseDTO<Any>> {
        logger.warn { "URL Not Found: ${e.httpMethod} ${e.requestURL}" }
        return ResponseEntity
            .status(GlobalErrorCode.URL_NOT_FOUND.status)
            .body(ResponseDTO.error(GlobalErrorCode.URL_NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    fun handleRuntimeErrors(e: Exception): ResponseEntity<ResponseDTO<Any>> {
        logger.error(e) { "Unhandled Exception: ${e.message}" } // Stack Trace 포함 로깅
        return ResponseEntity.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(
                ResponseDTO.error(
                    errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR,
                )
            )
    }
}