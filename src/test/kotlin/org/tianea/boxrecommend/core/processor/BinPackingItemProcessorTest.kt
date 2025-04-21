package org.tianea.boxrecommend.core.processor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.tianea.boxrecommend.core.vo.Bin
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.Item
import org.tianea.boxrecommend.core.vo.ItemAssignment

@SpringBootTest
@ExtendWith(SpringExtension::class)
class BinPackingItemProcessorTest(
) {
    @Autowired
    private lateinit var processor: BinPackingItemProcessor

    @Test
    fun `processor is work`() {
        val items = listOf(
            Item(id = 1, width = 100, height = 10, length = 10),
            Item(id = 2, width = 100, height = 10, length = 10),
            Item(id = 3, width = 100, height = 10, length = 10),
        )

        val bins = listOf(
            Bin(id = 1, width = 100, height = 100, length = 100),
            Bin(id = 2, width = 100, height = 100, length = 100),
        )

        val solution = BinPackingSolution(
            id = 1,
            assignments = items.mapIndexed { idx, it -> ItemAssignment(id = idx, item = it) },
            bins = bins,
        )

        processor.process(solution)
    }
}