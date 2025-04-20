package org.tianea.boxrecommend.config

import org.junit.jupiter.api.Test
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.batch.core.BatchStatus
import kotlin.test.assertEquals

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class BatchConfigTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `binPackingJob 이 성공적으로 수행되는지 확인`() {
        val jobExecution = jobLauncherTestUtils.launchJob()
        assertEquals(BatchStatus.COMPLETED, jobExecution.status)
    }
}