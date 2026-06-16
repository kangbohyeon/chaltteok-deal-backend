package com.chaltteok.core.repository.user

import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long>, UserRepositoryCustom {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun findByNicknameAndPhone(nickname: String, phone: String): Optional<User>
    fun findByEmailAndNickname(email: String, nickname: String): Optional<User>
}