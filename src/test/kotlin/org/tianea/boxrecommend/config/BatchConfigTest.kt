package org.tianea.boxrecommend.config

import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class BatchConfigTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `binPackingStep이 성공적으로 수행되어야 한다`() {
        val jobExecution = jobLauncherTestUtils.launchStep("binPackingStep")
        assertEquals(BatchStatus.COMPLETED, jobExecution.status)
        jobExecution.stepExecutions.forEach { stepExecution ->
            println("read count : ${stepExecution.readCount}")
            println("write count : ${stepExecution.writeCount}")
        }
    }
}