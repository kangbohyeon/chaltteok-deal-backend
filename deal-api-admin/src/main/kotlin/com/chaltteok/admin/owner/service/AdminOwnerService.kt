package com.chaltteok.admin.owner.service

import com.chaltteok.core.domain.Owner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AdminOwnerService {
    fun findAll(pageable: Pageable): Page<Owner>
    fun findById(id: Long): Owner
}
