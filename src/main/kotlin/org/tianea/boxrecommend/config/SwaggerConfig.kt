package org.tianea.boxrecommend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Box Recommend API")
                    .description("""
                        # Box Recommendation API
                        
                        This API provides box recommendation services using bin packing algorithm.
                        
                        ## Features
                        - Run bin packing jobs for optimal box selection
                        - View job status and results
                        - Monitor system health
                        
                        ## Authentication
                        Currently, no authentication is required for this API.
                    """.trimIndent())
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("Tianea Team")
                            .email("support@tianea.org")
                            .url("https://github.com/tianea")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8888").description("Development server"),
                    Server().url("https://api.tianea.org").description("Production server")
                )
            )
            .components(
                Components()
                    .securitySchemes(
                        mapOf(
                            "bearerAuth" to SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                    )
            )
    }
}
