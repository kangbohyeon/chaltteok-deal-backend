package com.chaltteok.user.notice.service

import com.chaltteok.user.notice.dto.NoticeResponse

interface UserNoticeService {
    fun getVisibleNotices(): List<NoticeResponse>
    fun getNotice(noticeUuid: String): NoticeResponse
}
