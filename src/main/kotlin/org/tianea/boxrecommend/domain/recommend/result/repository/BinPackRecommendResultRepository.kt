package org.tianea.boxrecommend.domain.recommend.result.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import org.tianea.boxrecommend.domain.recommend.result.document.BinPackRecommendResult

@Repository
interface BinPackRecommendResultRepository :
    ElasticsearchRepository<BinPackRecommendResult, String>,
    BinPackRecommendResultCustomRepository

// 날짜별 조회를 위한 전용 리포지토리 추가
interface BinPackRecommendResultCustomRepository {
    fun saveWithDateIndex(document: BinPackRecommendResult)
}
