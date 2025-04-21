package org.tianea.boxrecommend.domain.recommend.result.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendResultSku

@Repository
interface RecommendResultSkuRepository : JpaRepository<RecommendResultSku, Long> {
}