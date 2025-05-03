package org.tianea.boxrecommend.domain.recommend.result.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object IndexNameGenerator {
    private const val BASE_INDEX_NAME = "bin-pack-recommend-result"
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    fun generateIndexName(date: LocalDate = LocalDate.now()): String {
        return "${BASE_INDEX_NAME}-${date.format(DATE_FORMATTER)}"
    }
    
    fun generateAliasName(): String {
        return BASE_INDEX_NAME
    }
}
