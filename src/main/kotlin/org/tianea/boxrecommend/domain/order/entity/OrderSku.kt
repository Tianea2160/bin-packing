package org.tianea.boxrecommend.domain.order.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.tianea.boxrecommend.domain.common.BaseTimeEntity

@Entity
@Table(name = "order_sku")
class OrderSku(
    @Id
    @Column(name = "order_sku_id", nullable = false)
    val id: Long,
    @Column(name = "order_id", nullable = false)
    val orderId: Long,
    @Column(name = "sku_id", nullable = false)
    val skuId: Long,
    @Column(name = "requested_quantity", nullable = false)
    val requestedQuantity: Long,
) : BaseTimeEntity()