package com.chaltteok.user.popup.service

import com.chaltteok.user.popup.dto.PopupResponse

interface UserPopupService {
    fun getActivePopups(): List<PopupResponse>
}
