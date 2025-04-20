package org.tianea.boxrecommend.domain.box.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.tianea.boxrecommend.domain.common.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(name = "box")
class Box(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id")
    val id: Long,
    @Column(name = "name")
    val name: String,
    @Column(name = "weight")
    val weight: Long,
    @Column(name = "width")
    val width: Long,
    @Column(name = "height")
    val height: Long,
    @Column(name = "length")
    val length: Long,
    @Column(name = "shape")
    val shape: String,
) : BaseTimeEntity()