package com.chaltteok.owner.popup.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Notice
import com.chaltteok.core.repository.notice.NoticeRepository
import com.chaltteok.owner.popup.dto.PopupRequest
import com.chaltteok.owner.popup.dto.PopupResponse
import com.chaltteok.owner.popup.enums.PopupErrorCode
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerPopupServiceImpl(
    private val noticeRepository: NoticeRepository,
) : OwnerPopupService {

    @Transactional(readOnly = true)
    override fun getAll(): List<PopupResponse> =
        noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .map { PopupResponse.from(it) }

    @Transactional
    override fun create(request: PopupRequest) {
        val notice = Notice(
            title = request.title,
            content = request.content,
            isVisible = request.isVisible,
            location = request.location,
            startDate = request.startDate,
            endDate = request.endDate,
            startTime = request.startTime,
            endTime = request.endTime,
        )
        noticeRepository.save(notice)
    }

    @Transactional
    override fun update(popupUuid: String, request: PopupRequest) {
        val notice = noticeRepository.findByNoticeUuid(popupUuid)
            ?: throw BusinessException(PopupErrorCode.POPUP_NOT_FOUND)
        notice.title = request.title
        notice.content = request.content
        notice.isVisible = request.isVisible
        notice.location = request.location
        notice.startDate = request.startDate
        notice.endDate = request.endDate
        notice.startTime = request.startTime
        notice.endTime = request.endTime
    }

    @Transactional
    override fun delete(popupUuid: String) {
        val notice = noticeRepository.findByNoticeUuid(popupUuid)
            ?: throw BusinessException(PopupErrorCode.POPUP_NOT_FOUND)
        noticeRepository.delete(notice)
    }
}
