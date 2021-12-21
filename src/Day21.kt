import kotlin.math.max
import kotlin.math.min

fun main() {

    fun part1(input: Pair<Int, Int>): Int {
        val die = DeterministicDie()
        var positions = input
        var score = 0 to 0
        while (max(score.first, score.second) < 1000) {
            val rollSum = 1.rangeTo(3).sumOf { die.roll() }
            if ((die.rollCount / 3) % 2 == 1) {
                positions = (((positions.first + rollSum - 1) % 10) + 1) to positions.second
                score = (score.first + positions.first) to score.second
            } else {
                positions = positions.first to (((positions.second + rollSum - 1) % 10) + 1)
                score = score.first to (score.second + positions.second)

            }
        }
        return min(score.first, score.second) * die.rollCount
    }

    fun part2(input: Pair<Int, Int>): Long {
        val (p1, p2) = GameState(input, 0 to 0, true).winCounts()
        return max(p1, p2)
    }

    // test if implementation meets criteria from the description, like:
    check(part1(4 to 8) == 739785)
    check(part2(4 to 8) == 444356092776315)

    println(part1(3 to 7))
    println(part2(3 to 7))
}

private typealias WinCounts = Pair<Long, Long>
operator fun WinCounts.times(factor: Int) = (first * factor) to (second * factor)
operator fun WinCounts.plus(other: WinCounts) = (first + other.first) to (second + other.second)

private data class GameState(val positions: Pair<Int, Int>, val score: Pair<Int, Int>, val player1IsNext: Boolean) {

    fun winCounts(): WinCounts {
        cache[this]?.let { return it }
        return (
                if (score.first >= 21)
                    (1L to 0L)
                else if (score.second >= 21)
                    (0L to 1L)
                else
                    newStates().fold(0L to 0L) { acc, pair -> acc + pair.first.winCounts() * pair.second }
                )
            .also { cache[this] = it }
    }

    private fun newStates() =
        listOf(
            addRollSum(3) to 1,
            addRollSum(4) to 3,
            addRollSum(5) to 6,
            addRollSum(6) to 7,
            addRollSum(7) to 6,
            addRollSum(8) to 3,
            addRollSum(9) to 1
        )

    private fun addToPosition(start: Int, increment: Int) = ((start + increment - 1) % 10) + 1

    private fun addRollSum(rollSum: Int) =
        if (player1IsNext) {
            val nextPos1 = addToPosition(positions.first, rollSum)
            GameState(nextPos1 to positions.second, (score.first + nextPos1) to score.second, false)
        } else {
            val nextPos2 = addToPosition(positions.second, rollSum)
            GameState(positions.first to nextPos2, score.first to (score.second + nextPos2), true)
        }

    companion object {
        private val cache = mutableMapOf<GameState, WinCounts>()
    }

}

private class DeterministicDie {
    private var nextRoll = 1
    var rollCount = 0

    fun roll(): Int {
        rollCount++
        val result = nextRoll
        nextRoll = if (nextRoll == 100) 1 else nextRoll + 1
        return result
    }
}
