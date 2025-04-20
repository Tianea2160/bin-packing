package org.tianea.boxrecommend.domain.sku.entity

import jakarta.persistence.*
import org.tianea.boxrecommend.core.vo.Shape
import org.tianea.boxrecommend.domain.common.BaseTimeEntity

@Entity
@Table(name = "sku")
class Sku(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id", nullable = false)
    val id: Long,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "weight", nullable = false)
    val weight: Long,
    @Column(name = "width", nullable = false)
    val width: Long,
    @Column(name = "height", nullable = false)
    val height: Long,
    @Column(name = "length", nullable = false)
    val length: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "shape", nullable = false)
    val shape: Shape = Shape.BOX
) : BaseTimeEntity()