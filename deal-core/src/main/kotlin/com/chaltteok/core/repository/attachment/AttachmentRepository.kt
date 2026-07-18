package com.chaltteok.core.repository.attachment

import com.chaltteok.core.domain.Attachment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AttachmentRepository : JpaRepository<Attachment, Long> {
    fun findAllByReferenceUuidAndAttachmentType(referenceUuid: String, attachmentType: String): List<Attachment>

    fun findAllByReferenceUuidInAndAttachmentType(referenceUuids: List<String>, attachmentType: String): List<Attachment>

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Attachment a SET a.referenceUuid = :referenceUuid, a.attachmentType = :attachmentType WHERE a.attachmentUuid IN :uuids AND a.referenceUuid IS NULL")
    fun updateReferenceByUuids(
        @Param("uuids") uuids: List<String>,
        @Param("referenceUuid") referenceUuid: String,
        @Param("attachmentType") attachmentType: String,
    ): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Attachment a WHERE a.referenceUuid = :referenceUuid AND a.attachmentType = :attachmentType")
    fun deleteByReferenceUuidAndAttachmentType(
        @Param("referenceUuid") referenceUuid: String,
        @Param("attachmentType") attachmentType: String,
    )
}
