package org.tianea.boxrecommend.core.logging

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ChunkListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.stereotype.Component

@Component
class TransactionLoggingChunkListener : ChunkListener {

    private val logger = LoggerFactory.getLogger(TransactionLoggingChunkListener::class.java)

    override fun beforeChunk(context: ChunkContext) {
        logger.info(">>> 트랜잭션 시작: StepName=${context.stepContext.stepName}")
    }

    override fun afterChunk(context: ChunkContext) {
        logger.info("<<< 트랜잭션 종료: StepName=${context.stepContext.stepName}")
    }

    override fun afterChunkError(context: ChunkContext) {
        logger.warn("!!! 트랜잭션 오류 발생: StepName=${context.stepContext.stepName}")
    }
}