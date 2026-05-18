package com.chaltteok.core.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "tb_popup", uniqueConstraints = [UniqueConstraint(name = "uk_popup_uuid", columnNames = ["popup_uuid"])])
class Popup(
    @Column(name = "title", nullable = false, length = 200)
    var title: String,

    @Column(name = "content", columnDefinition = "TEXT")
    var content: String,

    @Column(name = "is_visible", nullable = false)
    var isVisible: Boolean = true,

    @Column(name = "location", length = 50)
    var location: String? = null,

    @Column(name = "start_date")
    var startDate: LocalDate? = null,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Column(name = "start_time")
    var startTime: LocalTime? = null,

    @Column(name = "end_time")
    var endTime: LocalTime? = null,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    var id: Long? = null

    @Column(name = "popup_uuid", nullable = false, length = 36)
    val popupUuid: String = UUID.randomUUID().toString()
}
