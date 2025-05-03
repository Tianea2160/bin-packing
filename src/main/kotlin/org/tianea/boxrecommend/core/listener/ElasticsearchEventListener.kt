package org.tianea.boxrecommend.core.listener

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import org.tianea.boxrecommend.core.event.RecommendResultSavedEvent
import org.tianea.boxrecommend.core.event.SkuInfo
import org.tianea.boxrecommend.domain.recommend.result.document.BinPackRecommendResult
import org.tianea.boxrecommend.domain.recommend.result.document.BinPackSkuDocument
import org.tianea.boxrecommend.domain.recommend.result.repository.BinPackRecommendResultCustomRepository
import org.tianea.boxrecommend.domain.recommend.result.repository.BinPackRecommendResultRepository

@Component
class ElasticsearchEventListener(
    private val binPackRecommendResultRepository: BinPackRecommendResultRepository,
) {
    private val logger = LoggerFactory.getLogger(ElasticsearchEventListener::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleRecommendResultSaved(event: RecommendResultSavedEvent) {
        try {
            val skuDocuments = event.skus.map { skuInfo ->
                BinPackSkuDocument(
                    skuId = skuInfo.skuId,
                    quantity = skuInfo.quantity
                )
            }
            
            val documentToSave = BinPackRecommendResult.from(event.recommendResult, skuDocuments)
            binPackRecommendResultRepository.saveWithDateIndex(documentToSave)
            
            logger.info("Successfully saved recommend result ${event.recommendResult.id} to Elasticsearch with date index")
        } catch (e: Exception) {
            logger.error("Failed to save recommend result ${event.recommendResult.id} to Elasticsearch", e)
            // 여기서 재시도 로직이나 큐에 저장하는 로직을 추가할 수 있음
        }
    }
}
