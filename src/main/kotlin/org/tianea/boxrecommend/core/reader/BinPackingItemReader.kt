package org.tianea.boxrecommend.core.reader

import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.tianea.boxrecommend.core.vo.Bin
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.Item
import org.tianea.boxrecommend.core.vo.ItemAssignment
import org.tianea.boxrecommend.domain.box.repository.BoxRepository
import org.tianea.boxrecommend.domain.order.entity.Order
import org.tianea.boxrecommend.domain.order.repository.OrderSkuRepository
import org.tianea.boxrecommend.domain.sku.repository.SkuRepository

@Configuration
class BinPackingReaderConfig(
    private val orderSkuRepository: OrderSkuRepository,
    private val skuRepository: SkuRepository,
    private val boxRepository: BoxRepository,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    @StepScope
    fun binPackingItemReader(): ItemStreamReader<BinPackingSolution> {
        return JpaPagingItemReaderBuilder<Order>()
            .name("orderReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT o FROM Order o")
            .pageSize(10)
            .build()
            .let { orderReader ->
                OrderBasedBinPackingReader(
                    orderReader = orderReader,
                    orderSkuRepository = orderSkuRepository,
                    skuRepository = skuRepository,
                    boxRepository = boxRepository
                )
            }
    }
}

class OrderBasedBinPackingReader(
    private val orderReader: JpaPagingItemReader<Order>,
    private val orderSkuRepository: OrderSkuRepository,
    private val skuRepository: SkuRepository,
    private val boxRepository: BoxRepository
) : ItemStreamReader<BinPackingSolution> {

    override fun read(): BinPackingSolution? {
        val order = orderReader.read() ?: return null

        // 해당 Order에 포함된 OrderSku 조회
        val orderSkus = orderSkuRepository.findByOrderId(order.id)

        // OrderSku에 해당하는 SKU 정보 조회
        val skuMap = skuRepository.findAllById(orderSkus.map { it.skuId })
            .associateBy { it.id }

        // 모든 박스 정보 조회
        val bins = boxRepository.findAll().map { Bin.from(it) }

        // OrderSku에 따라 Item 생성 (수량만큼 생성)
        val items = orderSkus
            .flatMapIndexed { index, orderSku ->
                val sku = skuMap[orderSku.skuId] ?: throw IllegalStateException("SKU not found: ${orderSku.skuId}")

                // 요청된 수량만큼 Item 생성
                (0 until orderSku.requestedQuantity).map { count -> Item.from(sku) }
            }

        // ItemAssignment 생성
        val assignments = items.mapIndexed { idx, item -> ItemAssignment(id = idx, item = item) }

        // BinPackingSolution 생성
        return BinPackingSolution(
            id = order.id,
            assignments = assignments,
            bins = bins
        )
    }

    override fun open(executionContext: ExecutionContext) {
        orderReader.open(executionContext)
    }

    override fun update(executionContext: ExecutionContext) {
        orderReader.update(executionContext)
    }

    override fun close() {
        orderReader.close()
    }
}