import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        val target = positions.sorted()[(positions.size - 1) / 2]
        return positions.sumOf { abs(it - target) }
    }

    fun part2(input: List<String>): Int {
        fun Int.gaussSum() = (this * this + this) / 2
        val positions = input.first().split(",").map { it.toInt() }
        val target = (positions.sum() + 1) / positions.size
        return positions.sumOf { abs(it - target).gaussSum() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

