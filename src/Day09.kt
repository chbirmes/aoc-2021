fun main() {

    fun calculateLowPoints(input: List<String>) = input.indices.map { x ->
        input.first().indices.map { y -> Point(x, y, input) }
    }
        .flatten()
        .filter { point ->
            point.neighbors().all {
                it.value() > point.value()
            }
        }

    fun part1(input: List<String>) = calculateLowPoints(input).sumOf { it.value() + 1 }

    fun Set<Point>.basinNeighbors(visited: Set<Point>) =
        map { it.neighbors() }
            .flatten()
            .filter { it.value() < 9 && it !in visited }
            .toSet()

    tailrec fun recursivelyVisitBasinNeighbors(next: Set<Point>, previouslyVisited: Set<Point>): Set<Point> {
        val visited = previouslyVisited + next
        val neighbors = next.basinNeighbors(visited)
        return if (neighbors.isEmpty())
            visited
        else
            recursivelyVisitBasinNeighbors(neighbors, visited)
    }

    fun basinContaining(start: Point) = recursivelyVisitBasinNeighbors(setOf(start), setOf())

    fun part2(input: List<String>) = calculateLowPoints(input)
        .map { basinContaining(it).size }
        .sortedDescending()
        .take(3)
        .reduce(Int::times)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private data class Point(val x: Int, val y: Int, val grid: List<String>) {

    private fun Pair<Int, Int>.isInBounds() = first in grid.indices && second in grid.first().indices

    fun value() = grid[x][y].digitToInt()

    fun neighbors(): List<Point> {
        return listOf(
            x - 1 to y,
            x to y - 1,
            x + 1 to y,
            x to y + 1
        )
            .filter { it.isInBounds() }
            .map { (x, y) -> Point(x, y, grid) }
    }
}
