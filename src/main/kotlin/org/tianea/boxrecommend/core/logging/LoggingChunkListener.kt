package org.tianea.boxrecommend.core.logging

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ChunkListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.stereotype.Component

@Component
class LoggingChunkListener : ChunkListener {

    private val logger = LoggerFactory.getLogger(LoggingChunkListener::class.java)

    override fun beforeChunk(context: ChunkContext) {
        logger.info(">>> chunk 시작: read count=${context.stepContext.stepExecution.readCount}")
        logger.info(">>> chunk 시작: write count=${context.stepContext.stepExecution.writeCount}")
    }

    override fun afterChunk(context: ChunkContext) {
        logger.info(">>> chunk 종료: read count=${context.stepContext.stepExecution.readCount}")
        logger.info(">>> chunk 종료: write count=${context.stepContext.stepExecution.writeCount}")
    }

    override fun afterChunkError(context: ChunkContext) {
    }
}