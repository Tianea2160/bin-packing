package org.tianea.boxrecommend.domain.sku.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tianea.boxrecommend.domain.sku.entity.Sku

@Repository
interface SkuRepository : JpaRepository<Sku, Long> {
}