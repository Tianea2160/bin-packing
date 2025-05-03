package org.tianea.boxrecommend.core.processor

import org.optaplanner.core.api.solver.SolverFactory
import org.slf4j.LoggerFactory.*
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import org.tianea.boxrecommend.core.vo.BinPackingSolution

@Component
class BinPackingItemProcessor(
    private val solverFactory: SolverFactory<BinPackingSolution>,
): ItemProcessor<BinPackingSolution, BinPackingSolution> {
    private val logger = getLogger(BinPackingItemProcessor::class.java)
    override fun process(solution: BinPackingSolution): BinPackingSolution? {
        val solver = solverFactory.buildSolver()
        val result = solver.solve(solution)

        if (result.isNotFeasible()) {
            val scoreDetail = result.score.toShortString()
            logger.warn("BinPacking 실패 - 해결 불가능한 해입니다. Score: $scoreDetail")
        }

        return result
    }
}