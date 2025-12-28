package com.chaltteok.core.repository.user

import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, UserRepositoryCustom {
}