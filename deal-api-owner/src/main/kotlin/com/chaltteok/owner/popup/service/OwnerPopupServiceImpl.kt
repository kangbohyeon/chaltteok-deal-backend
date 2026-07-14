package com.chaltteok.owner.popup.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Popup
import com.chaltteok.core.repository.popup.PopupRepository
import com.chaltteok.owner.popup.dto.PopupRequest
import com.chaltteok.owner.popup.dto.PopupResponse
import com.chaltteok.owner.popup.enums.PopupErrorCode
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerPopupServiceImpl(
    private val popupRepository: PopupRepository,
) : OwnerPopupService {

    @Transactional(readOnly = true)
    override fun getAll(): List<PopupResponse> =
        popupRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .map { PopupResponse.from(it) }

    @Transactional(readOnly = true)
    override fun getPopup(popupUuid: String): PopupResponse {
        val popup = popupRepository.findByPopupUuid(popupUuid)
            ?: throw BusinessException(PopupErrorCode.POPUP_NOT_FOUND)
        return PopupResponse.from(popup)
    }

    @Transactional
    override fun create(request: PopupRequest) {
        val popup = Popup(
            title = request.title,
            content = request.content,
            isVisible = request.isVisible,
            location = request.location,
            startDate = request.startDate,
            endDate = request.endDate,
            startTime = request.startTime,
            endTime = request.endTime,
        )
        popupRepository.save(popup)
    }

    @Transactional
    override fun update(popupUuid: String, request: PopupRequest) {
        val popup = popupRepository.findByPopupUuid(popupUuid)
            ?: throw BusinessException(PopupErrorCode.POPUP_NOT_FOUND)
        popup.title = request.title
        popup.content = request.content
        popup.isVisible = request.isVisible
        popup.location = request.location
        popup.startDate = request.startDate
        popup.endDate = request.endDate
        popup.startTime = request.startTime
        popup.endTime = request.endTime
    }

    @Transactional
    override fun delete(popupUuid: String) {
        val popup = popupRepository.findByPopupUuid(popupUuid)
            ?: throw BusinessException(PopupErrorCode.POPUP_NOT_FOUND)
        popupRepository.delete(popup)
    }
}
