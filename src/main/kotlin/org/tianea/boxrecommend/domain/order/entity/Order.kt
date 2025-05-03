package org.tianea.boxrecommend.domain.order.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.tianea.boxrecommend.domain.common.BaseTimeEntity

@Entity
@Table(name = "orders")
class Order(
    @Id
    @Column(name = "order_id", nullable = false)
    val id: Long,
    @Column(name = "title", nullable = false)
    val title: String,
) : BaseTimeEntity()

