import kotlin.math.abs

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
        val lines = input.toLines()
            .filter { it.isHorizontal() || it.isVertical() }
        val grid = gridContaining(lines)
        return grid
            .count { (x, y) ->
                lines.count { it.covers(x, y) } >= 2
            }
    }

    fun part2(input: List<String>): Int {
        val lines = input.toLines()
        val grid = gridContaining(lines)
        return grid
            .count { (x, y) ->
                lines.count { it.covers(x, y) } >= 2
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

class Line(private val x1: Int, private val y1: Int, private val x2: Int, private val y2: Int) {

    val xRange: IntRange = if (x2 < x1) x2..x1 else x1..x2
    val yRange: IntRange = if (y2 < y1) y2..y1 else y1..y2

    fun isHorizontal() = y1 == y2

    fun isVertical() = x1 == x2

    fun covers(x: Int, y: Int): Boolean {
        return if (isHorizontal()) y == y1 && x in xRange
        else if (isVertical()) x == x1 && y in yRange
        else x in xRange && y in yRange && abs(x - x1) == abs(y - y1)
    }
}

private fun Iterable<String>.toLines(): List<Line> {
    return map { line ->
        line.split(",", " -> ")
            .map { it.toInt() }
    }
        .map {
            Line(it[0], it[1], it[2], it[3])
        }
}
