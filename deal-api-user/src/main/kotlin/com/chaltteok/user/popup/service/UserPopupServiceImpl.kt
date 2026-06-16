package com.chaltteok.user.popup.service

import com.chaltteok.core.repository.popup.PopupRepository
import com.chaltteok.user.popup.dto.PopupResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class UserPopupServiceImpl(
    private val popupRepository: PopupRepository,
) : UserPopupService {

    @Transactional(readOnly = true)
    override fun getActivePopups(): List<PopupResponse> =
        popupRepository.findActivePopups(LocalDate.now(), LocalTime.now()).map { PopupResponse.from(it) }
}
