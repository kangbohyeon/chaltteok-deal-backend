package com.chaltteok.owner.notice.service

import com.chaltteok.owner.notice.dto.NoticeRequest
import com.chaltteok.owner.notice.dto.NoticeResponse

interface OwnerNoticeService {
    fun getAll(): List<NoticeResponse>
    fun create(request: NoticeRequest)
    fun update(noticeUuid: String, request: NoticeRequest)
    fun delete(noticeUuid: String)
}
