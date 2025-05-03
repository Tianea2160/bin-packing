package org.tianea.boxrecommend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tianea.boxrecommend.controller.dto.JobResponse

@RestController
@RequestMapping("/box-recommend")
@Tag(
    name = "Box Recommendation", 
    description = "API for running box recommendation algorithms",
    externalDocs = io.swagger.v3.oas.annotations.ExternalDocumentation(
        description = "Read more about bin packing algorithm",
        url = "https://en.wikipedia.org/wiki/Bin_packing_problem"
    )
)
class BoxRecommendController(
    private val jobLauncher: JobLauncher,
    private val binPackingJob: Job,
) {

    @Operation(
        summary = "Run box recommendation job",
        description = """
            Executes the bin packing algorithm to recommend optimal box configurations.
            
            This endpoint starts a batch job that:
            1. Reads SKU data from the database
            2. Applies bin packing algorithm to find optimal box configurations
            3. Saves results to the database and Elasticsearch
            4. Returns job execution information
        """,
        operationId = "runBoxRecommendation"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", 
                description = "Successfully started the job",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = JobResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", 
                description = "Job execution failed",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = String::class, example = "{ \"error\": \"Job execution failed\" }")
                )]
            )
        ]
    )
    @PostMapping("/run", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun run(): JobResponse {
        val jobParameters = JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters()
        
        val jobExecution = jobLauncher.run(binPackingJob, jobParameters)
        
        return JobResponse(
            message = "Job started successfully",
            jobId = jobExecution.jobId,
            executionId = jobExecution.id
        )
    }
}
