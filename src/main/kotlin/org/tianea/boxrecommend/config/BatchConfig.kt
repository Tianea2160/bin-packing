package org.tianea.boxrecommend.config


import org.optaplanner.core.api.score.buildin.bendable.BendableScore
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.solver.SolverConfig
import org.optaplanner.core.config.solver.termination.TerminationConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.tianea.boxrecommend.core.constraint.BinPackingConstraintProvider
import org.tianea.boxrecommend.core.logging.TransactionLoggingChunkListener
import org.tianea.boxrecommend.core.vo.*
import org.tianea.boxrecommend.domain.box.repository.BoxRepository
import org.tianea.boxrecommend.domain.sku.repository.SkuRepository

fun buildXYProjectionLog(assignments: List<ItemAssignment>, bin: Bin) = buildString {
    val maxZ = assignments.maxOfOrNull { (it.z ?: 0) + it.rotatedDimensions().third } ?: bin.length
    for (z in 0 until maxZ) {
        val grid = Array(bin.height.toInt()) { Array<String?>(bin.width.toInt()) { null } }

        for (a in assignments) {
            if (a.bin?.id != bin.id || a.x == null || a.y == null || a.z == null) continue
            val (w, h, l) = a.rotatedDimensions()
            for (dz in 0 until l) {
                if (a.z!! + dz != z.toLong()) continue
                for (dy in 0 until h) {
                    for (dx in 0 until w) {
                        val y = (a.y!! + dy).toInt()
                        val x = (a.x!! + dx).toInt()
                        if (y in grid.indices && x in grid[0].indices) {
                            grid[y][x] = a.item.id.toString()
                        }
                    }
                }
            }
        }

        appendLine("Bin ${bin.id} [XY 평면 @ Z=$z]")
        for (row in grid.reversed()) {
            appendLine(row.joinToString(" | ", prefix = "| ", postfix = " |") { it ?: " " })
        }
        appendLine()
    }
}


enum class ConstraintPurpose(
    val level: Int,
    val isHard: Boolean = false,
    val description: String,
) {
    PHYSICAL_CONSTRAINT(0, true, "물리 제약 위반 (겹침, 공간 초과 등)"),
    WEIGHT_CONSTRAINT(1, true, "무게 제한 초과"),
    BIN_COUNT(0, false, "사용된 박스 수 최소화"),
    WASTED_VOLUME(1, false, "빈 공간 최소화"),
    STACK_STABILITY(2, false, "무거운 물건은 아래에"),
    ;

    val isSoft = isHard.not()

    companion object {
        val HARD_LEVELS = entries.count { it.isHard }
        val SOFT_LEVELS = entries.count { it.isSoft }
    }


    fun score(value: Int): BendableScore {
        return if (isHard) {
            BendableScore.ofHard(HARD_LEVELS, SOFT_LEVELS, level, value)
        } else {
            BendableScore.ofSoft(HARD_LEVELS, SOFT_LEVELS, level, value)
        }
    }
}

@Configuration
@EnableBatchProcessing
class BatchConfig(
    private val transactionLoggingChunkListener: TransactionLoggingChunkListener,
) {
    private val logger = LoggerFactory.getLogger(BatchConfig::class.java)

    @Bean("solverFactory")
    fun solverFactory(): SolverFactory<BinPackingSolution> = SolverFactory.create(
        SolverConfig()
            .withSolutionClass(BinPackingSolution::class.java)
            .withEntityClasses(ItemAssignment::class.java)
            .withConstraintProviderClass(BinPackingConstraintProvider::class.java)
            .withTerminationConfig(TerminationConfig().apply {
                unimprovedSecondsSpentLimit = 3L
            })
    )

    @Bean
    fun binPackingItemReader(
        skuRepository: SkuRepository,
        boxRepository: BoxRepository,
    ): ItemReader<BinPackingSolution> {
        val items = skuRepository.findAll().map { Item.from(it) }
        val bins = boxRepository.findAll().map { Bin.from(it) }

        val assignments = items.mapIndexed { idx, item -> ItemAssignment(id = idx, item = item) }
        val solution = BinPackingSolution(assignments, bins)
        return ListItemReader(listOf(solution))
    }

    @Bean
    fun binPackingItemProcessor(
        solverFactory: SolverFactory<BinPackingSolution>
    ): ItemProcessor<BinPackingSolution, BinPackingSolution> = ItemProcessor { solution ->
        val solver = solverFactory.buildSolver()
        val result = solver.solve(solution)
        if (result.isNotFeasible()) {
            val scoreDetail = result.score.toShortString()
            logger.warn("BinPacking 실패 - 해결 불가능한 해입니다. Score: $scoreDetail")
            throw IllegalStateException("불가능한 조합입니다. 해결할 수 없습니다. (score=$scoreDetail)")
        }
        result
    }

    @Bean
    fun binPackingItemWriter(): ItemWriter<BinPackingSolution> = ItemWriter { solutions ->
        solutions.forEach { result ->
            logger.info("=== Batch step completed ===")
            logger.info("Score: ${result.score}")

            val assignmentsByBin = result.assignments
                .filter { it.bin != null }
                .groupBy { it.bin!!.id }

            assignmentsByBin.forEach { (binId, assignments) ->
                logger.info("Bin ID: $binId")
                val bin = result.bins.find { it.id == binId }!!
                logger.info(buildXYProjectionLog(assignments, bin))
            }
        }
    }

    @Bean
    fun binPackingStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        binPackingItemReader: ItemReader<BinPackingSolution>,
        binPackingItemProcessor: ItemProcessor<BinPackingSolution, BinPackingSolution>,
        binPackingItemWriter: ItemWriter<BinPackingSolution>
    ): Step = StepBuilder("binPackingStep", jobRepository)
        .chunk<BinPackingSolution, BinPackingSolution>(1, transactionManager)
        .reader(binPackingItemReader)
        .processor(binPackingItemProcessor)
        .writer(binPackingItemWriter)
        .listener(transactionLoggingChunkListener)
        .faultTolerant()
        .retry(Exception::class.java)
        .retryLimit(3)
        .build()

    @Bean
    fun binPackingJob(
        jobRepository: JobRepository,
        binPackingStep: Step
    ): Job = JobBuilder("binPackingJob", jobRepository)
        .incrementer(RunIdIncrementer())
        .start(binPackingStep)
        .build()
}

@Component
@EnableScheduling
class BatchScheduler(
    private val jobLauncher: JobLauncher,
    private val binPackingJob: Job,
) {
    @Scheduled(cron = "10 * * * * *")
    fun runBinPackingJob() {
        val jobParameters = JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(binPackingJob, jobParameters)
    }
}