package com.chaltteok.owner.notice.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Notice
import com.chaltteok.core.repository.notice.NoticeRepository
import com.chaltteok.owner.notice.dto.NoticeRequest
import com.chaltteok.owner.notice.dto.NoticeResponse
import com.chaltteok.owner.notice.enums.NoticeErrorCode
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerNoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
) : OwnerNoticeService {

    @Transactional(readOnly = true)
    override fun getAll(): List<NoticeResponse> =
        noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .map { NoticeResponse.from(it) }

    @Transactional
    override fun create(request: NoticeRequest) {
        noticeRepository.save(
            Notice(
                title = request.title,
                content = request.content,
                isVisible = request.isVisible,
            )
        )
    }

    @Transactional
    override fun update(noticeUuid: String, request: NoticeRequest) {
        val notice = noticeRepository.findByNoticeUuid(noticeUuid)
            ?: throw BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND)
        notice.title = request.title
        notice.content = request.content
        notice.isVisible = request.isVisible
    }

    @Transactional
    override fun delete(noticeUuid: String) {
        val notice = noticeRepository.findByNoticeUuid(noticeUuid)
            ?: throw BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND)
        noticeRepository.delete(notice)
    }
}
