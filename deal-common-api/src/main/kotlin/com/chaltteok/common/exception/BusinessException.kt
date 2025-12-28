package com.chaltteok.common.exception

import com.chaltteok.common.enums.ErrorCode

open class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)