package org.tianea.boxrecommend.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "Response for job execution",
    example = """
    {
        "message": "Job started successfully",
        "jobId": 123456789,
        "executionId": 987654321
    }
    """
)
data class JobResponse(
    @Schema(
        description = "Status message indicating the result of job execution",
        example = "Job started successfully",
        required = true,
        minLength = 1
    )
    val message: String,
    
    @Schema(
        description = "Unique identifier for the job instance",
        example = "123456789",
        required = true,
        minimum = "1"
    )
    val jobId: Long,
    
    @Schema(
        description = "Unique identifier for this specific job execution",
        example = "987654321",
        required = true,
        minimum = "1"
    )
    val executionId: Long
)
