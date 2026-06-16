package com.chaltteok.user.notice.service

import com.chaltteok.core.repository.notice.NoticeRepository
import com.chaltteok.user.notice.dto.NoticeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserNoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
) : UserNoticeService {

    @Transactional(readOnly = true)
    override fun getVisibleNotices(): List<NoticeResponse> =
        noticeRepository.findAllByIsVisibleTrueOrderByCreatedAtDesc().map { NoticeResponse.from(it) }
}
