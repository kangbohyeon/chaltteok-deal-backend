package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tb_attachment",
    uniqueConstraints = [UniqueConstraint(name = "uk_attachment_uuid", columnNames = ["attachment_uuid"])],
    indexes = [Index(name = "idx_attachment_reference", columnList = "reference_uuid,attachment_type")]
)
class Attachment(
    @Column(name = "file_url", nullable = false, length = 500) val fileUrl: String,
    @Column(name = "original_filename", nullable = false, length = 255) val originalFilename: String,
    @Column(name = "attachment_type", length = 20) var attachmentType: String? = null,
    @Column(name = "reference_uuid", length = 36) var referenceUuid: String? = null,
) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id") var id: Long? = null

    @Column(name = "attachment_uuid", nullable = false, length = 36)
    val attachmentUuid: String = UUID.randomUUID().toString()
}
