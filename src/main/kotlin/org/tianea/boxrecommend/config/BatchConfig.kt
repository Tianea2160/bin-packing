package org.tianea.boxrecommend.config


import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.solver.SolverConfig
import org.optaplanner.core.config.solver.termination.TerminationConfig
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager
import org.tianea.boxrecommend.core.constraint.BinPackingConstraintProvider
import org.tianea.boxrecommend.core.logging.LoggingChunkListener
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.ItemAssignment

@Configuration
@EnableBatchProcessing
class BatchConfig(
    private val loggingChunkListener: LoggingChunkListener,
) {

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
    fun binPackingStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        binPackingItemReader: ItemReader<BinPackingSolution>,
        binPackingItemProcessor: ItemProcessor<BinPackingSolution, BinPackingSolution>,
        binPackingItemWriter: ItemWriter<BinPackingSolution>
    ): Step = StepBuilder("binPackingStep", jobRepository)
        .chunk<BinPackingSolution, BinPackingSolution>(10, transactionManager)
        .reader(binPackingItemReader)
        .processor(binPackingItemProcessor)
        .writer(binPackingItemWriter)
        .listener(loggingChunkListener)
        .taskExecutor(
            SimpleAsyncTaskExecutor()
                .apply {
                    concurrencyLimit = 4
                }
        )
        .faultTolerant()
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
