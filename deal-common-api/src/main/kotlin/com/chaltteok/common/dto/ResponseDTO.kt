package com.chaltteok.common.dto

import com.chaltteok.common.enums.ResultType
import com.chaltteok.common.exception.ErrorCode
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDTO<T>(
    val result: ResultType,
    val errorCode: String? = null,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        // 1. 성공 응답 (데이터 있음)
        fun <T> success(data: T): ResponseDTO<T> {
            return ResponseDTO(
                result = ResultType.SUCCESS,
                data = data
            )
        }

        // 2. 성공 응답
        fun success(): ResponseDTO<Any> {
            return ResponseDTO(
                result = ResultType.SUCCESS
            )
        }

        // 3. 실패 응답 (에러 코드 + 메시지)
        fun error(errorCode: ErrorCode): ResponseDTO<Any> {
            return ResponseDTO(
                result = ResultType.ERROR,
                errorCode = errorCode.name,
                message = errorCode.message
            )
        }


        fun error(errorCode: ErrorCode, customMessage: String): ResponseDTO<Any> {
            return ResponseDTO(
                result = ResultType.ERROR,
                errorCode = errorCode.name,
                message = customMessage
            )
        }

        // 에러지만 상세 데이터를 함께 반환해야 할 때 사용
        fun <T> error(errorCode: ErrorCode, data: T): ResponseDTO<T> {
            return ResponseDTO(
                result = ResultType.ERROR,
                message = errorCode.message,
                data = data
            )
        }

    }
}
