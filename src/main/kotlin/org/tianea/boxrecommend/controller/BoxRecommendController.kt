package org.tianea.boxrecommend.controller

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/box-recommend")
class BoxRecommendController(
    private val jobLauncher: JobLauncher,
    private val binPackingJob: Job,
) {

    @PostMapping("/run")
    fun run() {
        val jobParameters = JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(binPackingJob, jobParameters)
    }
}
