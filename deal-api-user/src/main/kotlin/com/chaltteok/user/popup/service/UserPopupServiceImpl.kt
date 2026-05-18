package com.chaltteok.user.popup.service

import com.chaltteok.core.repository.notice.NoticeRepository
import com.chaltteok.user.popup.dto.PopupResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class UserPopupServiceImpl(
    private val noticeRepository: NoticeRepository,
) : UserPopupService {

    @Transactional(readOnly = true)
    override fun getActivePopups(): List<PopupResponse> =
        noticeRepository.findActiveNotices(LocalDate.now(), LocalTime.now()).map { PopupResponse.from(it) }
}
