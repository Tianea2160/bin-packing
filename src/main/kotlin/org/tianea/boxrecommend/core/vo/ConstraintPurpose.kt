package org.tianea.boxrecommend.core.vo

import org.optaplanner.core.api.score.buildin.bendable.BendableScore

enum class ConstraintPurpose(
    val level: Int,
    val isHard: Boolean = false,
    val description: String,
) {
    PHYSICAL_CONSTRAINT(0, true, "물리 제약 위반 (겹침, 공간 초과 등)"),
    WEIGHT_CONSTRAINT(1, true, "무게 제한 초과"),
    BIN_COUNT(0, false, "사용된 박스 수 최소화"),
    WASTED_VOLUME(1, false, "빈 공간 최소화"),
    STACK_STABILITY(2, false, "무거운 물건은 아래에"),
    ;

    val isSoft = isHard.not()

    companion object {
        val HARD_LEVELS = entries.count { it.isHard }
        val SOFT_LEVELS = entries.count { it.isSoft }
    }

    fun score(value: Int): BendableScore {
        return if (isHard) {
            BendableScore.ofHard(HARD_LEVELS, SOFT_LEVELS, level, value)
        } else {
            BendableScore.ofSoft(HARD_LEVELS, SOFT_LEVELS, level, value)
        }
    }
}