package org.tianea.boxrecommend.domain.order.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tianea.boxrecommend.domain.order.entity.Order

@Repository
interface OrderRepository : JpaRepository<Order, Long>

