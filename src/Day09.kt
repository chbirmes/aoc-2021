fun main() {

    fun calculateLowPoints(input: List<String>) = input.mapIndexed { x, row ->
        row.mapIndexed { y, char ->
            val current = char.digitToInt()
            Point(x, y).let { point ->
                if (point.neighborsIn(input).all { it.valueIn(input) > current })
                    point
                else
                    null
            }
        }
    }
        .flatten()
        .filterNotNull()

    fun part1(input: List<String>) = calculateLowPoints(input).sumOf { it.valueIn(input) + 1 }

    fun Point.basinNeighbors(input: List<String>, reached: Set<Point>) =
        neighborsIn(input).filter { it.valueIn(input) < 9 && it !in reached }

    fun basinSize(start: Point, input: List<String>, reached: MutableSet<Point>): Int {
        val neighbors = start.basinNeighbors(input, reached)
            .also { reached.addAll(it) }
        return if (neighbors.isEmpty())
            1
        else
            1 + neighbors.sumOf { basinSize(it, input, reached) }

    }

    fun part2(input: List<String>) = calculateLowPoints(input)
        .map { basinSize(it, input, mutableSetOf(it)) }
        .sortedDescending()
        .take(3)
        .reduce { x, y -> x * y }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private typealias Point = Pair<Int, Int>

private fun Point.isInBounds(input: List<String>) = first in input.indices && second in input.first().indices

private fun Point.valueIn(input: List<String>) = input[first][second].digitToInt()

private fun Point.neighborsIn(input: List<String>): List<Point> {
    return listOf(
        first - 1 to second,
        first to second - 1,
        first + 1 to second,
        first to second + 1
    ).filter { it.isInBounds(input) }
}
