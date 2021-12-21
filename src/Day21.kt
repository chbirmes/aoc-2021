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
        var multiverse = Multiverse(
            mapOf(
                Universe(input, 0 to 0, true) to 1
            )
        )
        while (!multiverse.finished()) {
            println("looking at ${multiverse.universeCounts.size} different universes")
            multiverse = multiverse.evolve()
        }
        return max(multiverse.player1WinCount(), multiverse.player2WinCount())
    }

    // test if implementation meets criteria from the description, like:
    check(part1(4 to 8) == 739785)
    check(part2(4 to 8) == 444356092776315)

    println(part1(3 to 7))
    println(part2(3 to 7))
}

private data class Universe(val positions: Pair<Int, Int>, val score: Pair<Int, Int>, val player1IsNext: Boolean) {

    private fun addToPosition(start: Int, increment: Int) = ((start + increment - 1) % 10) + 1

    private fun addRollSum(rollSum: Int) =
        if (player1IsNext) {
            val nextPos1 = addToPosition(positions.first, rollSum)
            Universe(nextPos1 to positions.second, (score.first + nextPos1) to score.second, false)
        } else {
            val nextPos2 = addToPosition(positions.second, rollSum)
            Universe(positions.first to nextPos2, score.first to (score.second + nextPos2), true)
        }

    fun evolve(factor: Long) =
        if (player1HasWon() == null)
            Multiverse(
                mapOf(
                    addRollSum(3) to 1 * factor,
                    addRollSum(4) to 3 * factor,
                    addRollSum(5) to 6 * factor,
                    addRollSum(6) to 7 * factor,
                    addRollSum(7) to 6 * factor,
                    addRollSum(8) to 3 * factor,
                    addRollSum(9) to 1 * factor,
                )
            )
        else
            Multiverse(mapOf(this to factor))

    fun player1HasWon() =
        if (score.first >= 21) true
        else if (score.second >= 21) false
        else null

}

private class Multiverse(val universeCounts: Map<Universe, Long>) {

    fun evolve() =
        universeCounts
            .map { (universe, count) -> universe.evolve(count) }
            .reduce(Multiverse::plus)

    fun finished() = universeCounts.keys.all { it.player1HasWon() != null }

    fun player1WinCount() = universeCounts.entries.sumOf { (universe, count) ->
        (if (universe.player1HasWon() == true) 1 else 0) * count
    }

    fun player2WinCount() = universeCounts.entries.sumOf { (universe, count) ->
        (if (universe.player1HasWon() == false) 1 else 0) * count
    }

    private operator fun plus(other: Multiverse) =
        Multiverse((universeCounts.keys + other.universeCounts.keys).associateWith {
            universeCounts.getOrDefault(it, 0) + other.universeCounts.getOrDefault(it, 0)
        })

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
