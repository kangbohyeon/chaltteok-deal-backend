package com.chaltteok.user.notice.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.repository.notice.NoticeRepository
import com.chaltteok.user.notice.dto.NoticeResponse
import com.chaltteok.user.notice.enums.NoticeErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserNoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
) : UserNoticeService {

    @Transactional(readOnly = true)
    override fun getVisibleNotices(): List<NoticeResponse> =
        noticeRepository.findAllByIsVisibleTrueOrderByCreatedAtDesc().map { NoticeResponse.from(it) }

    @Transactional(readOnly = true)
    override fun getNotice(noticeUuid: String): NoticeResponse {
        val notice = noticeRepository.findByNoticeUuid(noticeUuid)
            ?: throw BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND)
        if (!notice.isVisible) throw BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND)
        return NoticeResponse.from(notice)
    }
}
