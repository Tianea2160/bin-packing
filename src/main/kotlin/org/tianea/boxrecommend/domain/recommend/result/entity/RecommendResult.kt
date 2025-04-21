package org.tianea.boxrecommend.domain.recommend.result.entity

import jakarta.persistence.*
import org.tianea.boxrecommend.domain.common.BaseTimeEntity

@Entity
@Table(name = "recommend_result")
class RecommendResult(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "solution_id", nullable = false)
    val solutionId: Long,

    @Column(name = "recommended_bin_id", nullable = false)
    val recommendedBinId: Long,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: RecommendStatus,

    @Column(name = "score", nullable = true)
    val score: Long
) : BaseTimeEntity()


@Entity
@Table(name = "recommend_result_sku")
class RecommendResultSku(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "recommend_result_id", nullable = false)
    val recommendResultId: Long,

    @Column(name = "sku_id", nullable = false)
    val skuId: Long,

    @Column(name = "quantity", nullable = false)
    val quantity: Int
) : BaseTimeEntity()

enum class RecommendStatus {
    SUCCESS,
    FAILURE
}