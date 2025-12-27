package com.chaltteok.common.exception

import org.springframework.http.HttpStatus

interface ErrorCode {
    val name : String           // Enum의 이름
    val message : String        // client에 보여줄 message
    val status : HttpStatus     // status
}