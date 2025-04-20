package org.tianea.boxrecommend.core

import org.junit.jupiter.api.Test
import org.optaplanner.test.api.score.stream.ConstraintVerifier
import org.tianea.boxrecommend.config.Bin
import org.tianea.boxrecommend.config.BinPackingConstraintProvider
import org.tianea.boxrecommend.config.BinPackingSolution
import org.tianea.boxrecommend.config.Item
import org.tianea.boxrecommend.config.ItemAssignment
import org.tianea.boxrecommend.config.Rotation
import org.tianea.boxrecommend.config.Shape

class BinPackingConstraintProviderTest {
    val provider = BinPackingConstraintProvider()
    private val constraintVerifier = ConstraintVerifier
        .build(
            provider,
            BinPackingSolution::class.java,
            ItemAssignment::class.java
        )

    @Test
    fun `itemMustFitInBin should penalize when item exceeds bin dimensions`() {
        val bin = Bin(id = 1, width = 5, height = 5, length = 5)
        val item = Item(id = 1, width = 10, height = 10, length = 10, shape = Shape.BOX)

        val assignment = ItemAssignment(
            id = 1,
            item = item,
            bin = bin,
            x = 0, y = 0, z = 0,
            rotation = Rotation.XYZ
        )

        constraintVerifier
            .verifyThat { p, f -> p.itemMustFitInBin(f) }
            .given(assignment)
            .penalizesBy(1)
    }

    @Test
    fun `noOverlap should penalize when two items overlap`() {
        val bin = Bin(id = 1, width = 10, height = 10, length = 10)
        val item1 = Item(id = 1, width = 5, height = 5, length = 5, shape = Shape.BOX)
        val item2 = Item(id = 2, width = 5, height = 5, length = 5, shape = Shape.BOX)

        val assignment1 = ItemAssignment(
            id = 1,
            item = item1,
            bin = bin,
            x = 0, y = 0, z = 0,
            rotation = Rotation.XYZ
        )

        val assignment2 = ItemAssignment(
            id = 2,
            item = item2,
            bin = bin,
            x = 2, y = 2, z = 2, // overlaps with item1
            rotation = Rotation.XYZ
        )

        constraintVerifier
            .verifyThat { p, f -> p.noOverlap(f) }
            .given(assignment1, assignment2)
            .penalizesBy(1)
    }

    @Test
    fun `binCapacityExceeded should penalize when total item volume exceeds bin volume`() {
        val bin = Bin(id = 1, width = 2, height = 2, length = 2)
        val item1 = Item(id = 1, width = 2, height = 2, length = 2, shape = Shape.BOX)
        val item2 = Item(id = 2, width = 2, height = 2, length = 2, shape = Shape.BOX)

        val assignment1 = ItemAssignment(
            id = 1,
            item = item1,
            bin = bin,
            x = 0, y = 0, z = 0,
            rotation = Rotation.XYZ
        )

        val assignment2 = ItemAssignment(
            id = 2,
            item = item2,
            bin = bin,
            x = 2, y = 2, z = 2,
            rotation = Rotation.XYZ
        )

        constraintVerifier
            .verifyThat { p, f -> p.binCapacityExceeded(f) }
            .given(assignment1, assignment2)
            .penalizesBy(1)
    }

    @Test
    fun `binWeightLimitExceeded should penalize when bin is overloaded`() {
        val bin = Bin(id = 1, width = 10, height = 10, length = 10, maxWeight = 10)
        val item1 = Item(id = 1, width = 2, height = 2, length = 2, weight = 6, shape = Shape.BOX)
        val item2 = Item(id = 2, width = 2, height = 2, length = 2, weight = 6, shape = Shape.BOX)

        val assignment1 = ItemAssignment(id = 1, item = item1, bin = bin, x = 0, y = 0, z = 0, rotation = Rotation.XYZ)
        val assignment2 = ItemAssignment(id = 2, item = item2, bin = bin, x = 3, y = 0, z = 0, rotation = Rotation.XYZ)

        constraintVerifier
            .verifyThat { p, f -> p.binWeightLimitExceeded(f) }
            .given(assignment1, assignment2)
            .penalizesBy(1)
    }

    @Test
    fun `minimizeBinUsage should reward fewer bins used`() {
        val bin1 = Bin(id = 1, width = 10, height = 10, length = 10)
        val bin2 = Bin(id = 2, width = 10, height = 10, length = 10)

        val item1 = Item(id = 1, width = 2, height = 2, length = 2, shape = Shape.BOX)
        val item2 = Item(id = 2, width = 2, height = 2, length = 2, shape = Shape.BOX)

        val assignment1 = ItemAssignment(id = 1, item = item1, bin = bin1, x = 0, y = 0, z = 0, rotation = Rotation.XYZ)
        val assignment2 = ItemAssignment(id = 2, item = item2, bin = bin2, x = 0, y = 0, z = 0, rotation = Rotation.XYZ)

        constraintVerifier
            .verifyThat { p, f -> p.minimizeBinUsage(f) }
            .given(assignment1, assignment2)
            .penalizesBy(2) // two bins used → penalty 2
    }

    @Test
    fun `binWeightLimitExceeded should not penalize when weight is within limit`() {
        val bin = Bin(id = 1, width = 10, height = 10, length = 10, maxWeight = 15)
        val item1 = Item(id = 1, width = 2, height = 2, length = 2, weight = 7, shape = Shape.BOX)
        val item2 = Item(id = 2, width = 2, height = 2, length = 2, weight = 7, shape = Shape.BOX)

        val assignment1 = ItemAssignment(id = 1, item = item1, bin = bin, x = 0, y = 0, z = 0, rotation = Rotation.XYZ)
        val assignment2 = ItemAssignment(id = 2, item = item2, bin = bin, x = 4, y = 0, z = 0, rotation = Rotation.XYZ)

        constraintVerifier
            .verifyThat { p, f -> p.binWeightLimitExceeded(f) }
            .given(assignment1, assignment2)
            .penalizesBy(0)
    }

    @Test
    fun `minimizeBinUsage should penalize less when one bin is used`() {
        val bin = Bin(id = 1, width = 10, height = 10, length = 10)

        val item1 = Item(id = 1, width = 2, height = 2, length = 2, shape = Shape.BOX)
        val item2 = Item(id = 2, width = 2, height = 2, length = 2, shape = Shape.BOX)

        val assignment1 = ItemAssignment(id = 1, item = item1, bin = bin, x = 0, y = 0, z = 0, rotation = Rotation.XYZ)
        val assignment2 = ItemAssignment(id = 2, item = item2, bin = bin, x = 3, y = 0, z = 0, rotation = Rotation.XYZ)

        constraintVerifier
            .verifyThat { p, f -> p.minimizeBinUsage(f) }
            .given(assignment1, assignment2)
            .penalizesBy(1) // one bin used → penalty 1
    }
}