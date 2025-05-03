package org.tianea.boxrecommend.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.IndexOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Component
import org.tianea.boxrecommend.domain.recommend.result.document.BinPackRecommendResult

@Component
class ElasticsearchIndexSetup(
    private val operations: ElasticsearchOperations
) {
    
    @EventListener(ApplicationReadyEvent::class)
    fun setupIndexTemplate() {
        val indexOps: IndexOperations = operations.indexOps(BinPackRecommendResult::class.java)
        
        // 인덱스가 없으면 생성
        if (!indexOps.exists()) {
            indexOps.create()
            indexOps.putMapping()
        }
    }
}
