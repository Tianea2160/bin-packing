package org.tianea.boxrecommend.core.writer

import org.slf4j.LoggerFactory
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.support.ListItemWriter
import org.springframework.stereotype.Component
import org.tianea.boxrecommend.core.vo.Bin
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.ItemAssignment
import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendResult
import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendResultSku
import org.tianea.boxrecommend.domain.recommend.result.entity.RecommendStatus
import org.tianea.boxrecommend.domain.recommend.result.repository.RecommendResultRepository
import org.tianea.boxrecommend.domain.recommend.result.repository.RecommendResultSkuRepository

@Component
class BinPackingItemWriter(
    private val recommendResultRepository: RecommendResultRepository,
    private val recommendResultSkuRepository: RecommendResultSkuRepository,
) : ListItemWriter<BinPackingSolution>() {
    private val logger = LoggerFactory.getLogger(BinPackingItemWriter::class.java)

    override fun write(chunk: Chunk<out BinPackingSolution>) {
        logger.info("${this.javaClass.simpleName} write: chunk size=${chunk.size()}")
        logger.info("completed :  solutions ids ${chunk.map { it.id }.joinToString(",")}")

        chunk.forEach { solution ->
            val successfulAssignments = solution.assignments.filter { it.bin != null }

            val result = RecommendResult(
                id = null,
                solutionId = solution.id,
                recommendedBinId = successfulAssignments.firstOrNull()?.bin?.id ?: -1L,
                status = if (solution.isFeasible()) RecommendStatus.SUCCESS else RecommendStatus.FAILURE,
                score = solution.score.hardScores().sum().toLong()
            )
            recommendResultRepository.save(result)

            val skus = successfulAssignments
                .groupBy { it.item.id }
                .map { (skuId, list) ->
                    RecommendResultSku(
                        id = null,
                        recommendResultId = result.id ?: -1,
                        skuId = skuId,
                        quantity = 1
                    )
                }
                .toMutableList()
            recommendResultSkuRepository.saveAll(skus)
        }
    }
}
