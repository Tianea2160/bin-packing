package org.tianea.boxrecommend.core.event

import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.domain.recommend.result.document.AssignmentDetail
import org.tianea.boxrecommend.domain.recommend.result.document.ScoreCoordinate
import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendResult

data class RecommendResultSavedEvent(
    val recommendResult: RecommendResult,
    val skus: List<SkuInfo>,
    val solution: BinPackingSolution,
    val scoreDescription: String,
    val scoreCoordinates: List<ScoreCoordinate>,
    val assignments: List<AssignmentDetail>
)

data class SkuInfo(
    val skuId: Long,
    val quantity: Int
)
