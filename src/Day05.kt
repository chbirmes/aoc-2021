import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun gridContaining(lines: List<Line>): List<Pair<Int, Int>> {
        val maxX = lines.maxOf { it.xRange.last }
        val maxY = lines.maxOf { it.yRange.last }
        return (0..maxX).map { x ->
            (0..maxY).map { y -> Pair(x, y) }
        }
            .flatten()
    }

    fun part1(input: List<String>): Int {
        val lines = input
            .map { Line(it) }
            .filter { it.isHorizontal() || it.isVertical() }
        val grid = gridContaining(lines)
        return grid.count { it.countCovering(lines) >=2 }
    }

    fun part2(input: List<String>): Int {
        val lines = input.map { Line(it) }
        val grid = gridContaining(lines)
        return grid.count { it.countCovering(lines) >=2 }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

private class Line(private val x1: Int, private val y1: Int, private val x2: Int, private val y2: Int) {

    val xRange: IntRange = min(x1, x2)..max(x1, x2)
    val yRange: IntRange = min(y1, y2)..max(y1, y2)

    fun isHorizontal() = y1 == y2

    fun isVertical() = x1 == x2

    fun covers(x: Int, y: Int): Boolean {
        return if (isHorizontal()) y == y1 && x in xRange
        else if (isVertical()) x == x1 && y in yRange
        else x in xRange && y in yRange && abs(x - x1) == abs(y - y1)
    }
}

private fun Line(string: String) = string.split(",", " -> ")
    .map { it.toInt() }
    .let { Line(it[0], it[1], it[2], it[3]) }

private fun Pair<Int, Int>.countCovering(lines: Iterable<Line>) = lines.count { it.covers(first, second) }