package org.tianea.boxrecommend.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = ["org.tianea.boxrecommend.domain.recommend.result.repository"])
class ElasticsearchConfig
