package org.tianea.boxrecommend.core.event

import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendResult

data class RecommendResultSavedEvent(
    val recommendResult: RecommendResult,
    val skus: List<SkuInfo>
)

data class SkuInfo(
    val skuId: Long,
    val quantity: Int
)
