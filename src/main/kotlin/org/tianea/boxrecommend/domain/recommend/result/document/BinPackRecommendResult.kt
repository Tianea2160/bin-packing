package org.tianea.boxrecommend.domain.recommend.result.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendResult
import java.time.LocalDateTime

@Document(indexName = "bin-pack-recommend-result")
data class BinPackRecommendResult(
    @Id
    val id: String? = null,
    
    @Field(type = FieldType.Long)
    val solutionId: Long,
    
    @Field(type = FieldType.Long)
    val recommendedBinId: Long,
    
    @Field(type = FieldType.Text)
    val status: String,
    
    @Field(type = FieldType.Long)
    val score: Long?,
    
    @Field(type = FieldType.Date)
    val createdAt: LocalDateTime,
    
    @Field(type = FieldType.Nested)
    val skus: List<BinPackSkuDocument>
) {
    companion object {
        fun from(result: RecommendResult, skus: List<BinPackSkuDocument>): BinPackRecommendResult {
            return BinPackRecommendResult(
                id = result.id?.toString(),
                solutionId = result.solutionId,
                recommendedBinId = result.recommendedBinId,
                status = result.status.name,
                score = result.score,
                createdAt = result.createdAt,
                skus = skus
            )
        }
    }
}

data class BinPackSkuDocument(
    @Field(type = FieldType.Long)
    val skuId: Long,
    
    @Field(type = FieldType.Integer)
    val quantity: Int
)
