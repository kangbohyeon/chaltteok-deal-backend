package com.chaltteok.owner.popup.service

import com.chaltteok.owner.popup.dto.PopupRequest
import com.chaltteok.owner.popup.dto.PopupResponse

interface OwnerPopupService {
    fun getAll(): List<PopupResponse>
    fun getPopup(popupUuid: String): PopupResponse
    fun create(request: PopupRequest)
    fun update(popupUuid: String, request: PopupRequest)
    fun delete(popupUuid: String)
}
