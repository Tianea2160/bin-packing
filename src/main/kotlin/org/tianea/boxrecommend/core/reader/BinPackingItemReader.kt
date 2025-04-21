package org.tianea.boxrecommend.core.reader

import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemStreamReader
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.item.support.SynchronizedItemStreamReader
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
    fun binPackingItemReader(): ItemStreamReader<BinPackingSolution> {
        val items = skuRepository.findAll().map { Item.from(it) }
        val bins = boxRepository.findAll().map { Bin.from(it) }
        val assignments = items.mapIndexed { idx, item -> ItemAssignment(id = idx, item = item) }

        val duplicated = (0 until 20)
            .map { idx -> BinPackingSolution(idx.toLong(), assignments, bins) }
            .toCollection(LinkedList())
        val customReader = CustomBinPackingItemReader(ListItemReader(duplicated))
        return SynchronizedItemStreamReader<BinPackingSolution>()
            .apply { setDelegate(customReader) }
    }
}

class CustomBinPackingItemReader<T>(
    private val reader: ListItemReader<T>
) : ItemStreamReader<T> {

    override fun read(): T? = reader.read()
}