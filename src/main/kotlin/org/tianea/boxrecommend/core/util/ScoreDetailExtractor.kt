package org.tianea.boxrecommend.core.util

import org.springframework.stereotype.Component
import org.tianea.boxrecommend.core.vo.BinPackingSolution
import org.tianea.boxrecommend.core.vo.ConstraintPurpose
import org.tianea.boxrecommend.domain.recommend.result.document.AssignmentDetail
import org.tianea.boxrecommend.domain.recommend.result.document.ScoreCoordinate

@Component
class ScoreDetailExtractor {

    fun extractScoreCoordinates(
        solution: BinPackingSolution
    ): Pair<String, List<ScoreCoordinate>> {
        val description = buildString {
            appendLine("Total Score: ${solution.score.toShortString()}")
            appendLine("Hard Score: ${solution.score.hardScores().joinToString("/")}")
            appendLine("Soft Score: ${solution.score.softScores().joinToString("/")}")
        }

        val scoreCoordinates = mutableListOf<ScoreCoordinate>()

        // ConstraintPurpose의 좌표계 기준으로 점수 수집
        ConstraintPurpose.entries.forEach { purpose ->
            val score = if (purpose.isHard) {
                solution.score.hardScores()[purpose.level]
            } else {
                solution.score.softScores()[purpose.level]
            }

            if (score != 0) {
                scoreCoordinates.add(
                    ScoreCoordinate(
                        level = purpose.level,
                        isHard = purpose.isHard,
                        score = score,
                        constraintName = purpose.name,
                        constraintDescription = purpose.description
                    )
                )
            }
        }

        return description to scoreCoordinates
    }

    fun extractAssignmentDetails(solution: BinPackingSolution): List<AssignmentDetail> {
        return solution.assignments.map { assignment ->
            AssignmentDetail(
                itemId = assignment.item.id,
                binId = assignment.bin?.id,
                x = assignment.x?.toInt(),
                y = assignment.y?.toInt(),
                z = assignment.z?.toInt(),
                rotation = assignment.rotation?.name
            )
        }
    }
}
