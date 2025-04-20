package org.tianea.boxrecommend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<BoxRecommendApplication>(*args)
}

@SpringBootApplication
class BoxRecommendApplication
