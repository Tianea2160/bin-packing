package org.tianea.boxrecommend.domain.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity(
    @CreatedDate
    @Column(
        name = "created_at",
        columnDefinition = "timestamp with time zone",
        updatable = false,
    )
    open val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(
        name = "updated_at",
        columnDefinition = "timestamp with time zone",
    )
    open var updatedAt: LocalDateTime = LocalDateTime.now()
)