package org.tianea.boxrecommend.core.reader

import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.tianea.boxrecommend.core.vo.Bin
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.Item
import org.tianea.boxrecommend.core.vo.ItemAssignment
import org.tianea.boxrecommend.domain.box.repository.BoxRepository
import org.tianea.boxrecommend.domain.sku.repository.SkuRepository
import java.util.*

@Configuration
class BinPackingReaderConfig(
    private val skuRepository: SkuRepository,
    private val boxRepository: BoxRepository,
) {

    @Bean
    @StepScope
    fun binPackingItemReader(): ItemReader<BinPackingSolution> {
        val items = skuRepository.findAll().map { Item.from(it) }
        val bins = boxRepository.findAll().map { Bin.from(it) }
        val assignments = items.mapIndexed { idx, item -> ItemAssignment(id = idx, item = item) }

        val duplicated = (0 until 10)
            .map { idx -> BinPackingSolution(idx.toLong(), assignments, bins) }
            .toCollection(LinkedList())
        return ListItemReader(duplicated)
    }
}