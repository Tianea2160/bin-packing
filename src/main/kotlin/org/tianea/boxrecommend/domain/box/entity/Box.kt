package org.tianea.boxrecommend.domain.box.entity

import jakarta.persistence.*
import org.tianea.boxrecommend.domain.common.BaseTimeEntity

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
    @Column(name = "max_weight")
    val maxWeight: Long,
) : BaseTimeEntity()