package com.chaltteok.core.repository.user

import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.Optional

interface UserRepository : JpaRepository<User, Long>, UserRepositoryCustom {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun findByNicknameAndPhone(nickname: String, phone: String): Optional<User>
    fun findByEmailAndNickname(email: String, nickname: String): Optional<User>

    @Modifying
    @Query(
        """
        UPDATE User u SET
            u.loginFailedCount = u.loginFailedCount + 1,
            u.lockedAt = CASE WHEN u.loginFailedCount + 1 >= :maxFailCount THEN :now ELSE u.lockedAt END
        WHERE u.id = :userId
        """
    )
    fun incrementFailedCountAndLockIfNeeded(
        @Param("userId") userId: Long,
        @Param("maxFailCount") maxFailCount: Int,
        @Param("now") now: LocalDateTime,
    ): Int

    @Modifying
    @Query("UPDATE User u SET u.loginFailedCount = 0 WHERE u.id = :userId")
    fun resetFailedCount(@Param("userId") userId: Long): Int
}