package org.tianea.boxrecommend.config


import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.api.solver.SolutionManager
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
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.transaction.PlatformTransactionManager
import org.tianea.boxrecommend.core.constraint.BinPackingConstraintProvider
import org.tianea.boxrecommend.core.logging.LoggingChunkListener
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.ItemAssignment
import java.util.concurrent.ThreadPoolExecutor

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
            // 병렬 처리 비활성화 (moveThreadCount 설정 제거)
            .withTerminationConfig(TerminationConfig().apply {
                unimprovedSecondsSpentLimit = 3L
                secondsSpentLimit = 20L
            })
    )

    @Bean
    fun solutionManager(solverFactory: SolverFactory<BinPackingSolution>): SolutionManager<BinPackingSolution, org.optaplanner.core.api.score.buildin.bendable.BendableScore> {
        return SolutionManager.create(solverFactory)
    }

    @Bean
    fun taskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
            .apply {
                corePoolSize = 2                 // 코어 스레드 수를 4에서 2로 감소
                maxPoolSize = 4                  // 최대 스레드 수를 8에서 4로 감소
                queueCapacity = 50               // 작업 큐 용량을 25에서 50으로 증가
                keepAliveSeconds = 30            // 유휴 스레드 제거 시간을 60초에서 30초로 감소
                setThreadNamePrefix("box-recommend-")
                setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
                setAwaitTerminationSeconds(60)
                setWaitForTasksToCompleteOnShutdown(true)
                initialize()
            }

        return executor
    }

    @Bean
    fun binPackingStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        binPackingItemReader: ItemReader<BinPackingSolution>,
        binPackingItemProcessor: ItemProcessor<BinPackingSolution, BinPackingSolution>,
        binPackingItemWriter: ItemWriter<BinPackingSolution>
    ): Step = StepBuilder("binPackingStep", jobRepository)
        .chunk<BinPackingSolution, BinPackingSolution>(5, transactionManager) // 청크 크기를 10에서 5로 감소하여 메모리 사용량 개선
        .reader(binPackingItemReader)
        .processor(binPackingItemProcessor)
        .writer(binPackingItemWriter)
        .listener(loggingChunkListener)
        .taskExecutor(taskExecutor())
        .faultTolerant()
        .skip(Exception::class.java) // 모든 예외 타입 처리
        .skipLimit(3) // 오류 발생 시 최대 3개 아이템까지 건너뛰기 허용
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
