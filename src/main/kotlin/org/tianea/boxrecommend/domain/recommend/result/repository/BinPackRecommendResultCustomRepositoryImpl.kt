package org.tianea.boxrecommend.domain.recommend.result.repository

import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.IndexQuery
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder
import org.springframework.stereotype.Repository
import org.tianea.boxrecommend.domain.recommend.result.document.BinPackRecommendResult
import org.tianea.boxrecommend.domain.recommend.result.util.IndexNameGenerator

@Repository
class BinPackRecommendResultCustomRepositoryImpl(
    private val operations: ElasticsearchOperations
) : BinPackRecommendResultCustomRepository {

    override fun saveWithDateIndex(document: BinPackRecommendResult) {
        val indexName = IndexNameGenerator.generateIndexName()
        val query: IndexQuery = IndexQueryBuilder()
            .withId(document.id.toString())
            .withObject(document)
            .build()

        operations.index(query, IndexCoordinates.of(indexName))
    }
}
