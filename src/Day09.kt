fun main() {

    fun part1(input: List<String>): Int {
        return input.mapIndexed { x, row ->
            row.mapIndexed { y, char ->
                val current = char.digitToInt()
                if ((x == 0 || input[x - 1][y].digitToInt() > current)
                    && (y == 0 || input[x][y - 1].digitToInt() > current)
                    && (x == input.size - 1 || input[x + 1][y].digitToInt() > current)
                    && (y == row.length - 1 || input[x][y + 1].digitToInt() > current)
                )
                    current + 1
                else 0
            }
        }
            .flatten()
            .sum()
    }

    fun Pair<Int, Int>.eligibleNeighbors(input: List<String>, reached: Set<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return listOf(
            first - 1 to second,
            first to second - 1,
            first + 1 to second,
            first to second + 1
        ).filter { (x, y) ->
            x in input.indices
                    && y in input.first().indices
                    && input[x][y].digitToInt() < 9
                    && x to y !in reached
        }
    }

    fun basinSize(start: Pair<Int, Int>, input: List<String>, reached: MutableSet<Pair<Int, Int>>): Int {
        val neighbors = start.eligibleNeighbors(input, reached)
        reached.addAll(neighbors)
        return if (neighbors.isEmpty()) {
            1
        } else {
            1 + neighbors.sumOf { basinSize(it, input, reached) }
        }
    }

    fun part2(input: List<String>): Int {
        val lowPoints = input.mapIndexed { x, row ->
            row.mapIndexed { y, char ->
                val current = char.digitToInt()
                if ((x == 0 || input[x - 1][y].digitToInt() > current)
                    && (y == 0 || input[x][y - 1].digitToInt() > current)
                    && (x == input.size - 1 || input[x + 1][y].digitToInt() > current)
                    && (y == row.length - 1 || input[x][y + 1].digitToInt() > current)
                )
                    x to y
                else null
            }
        }
            .flatten()
            .filterNotNull()

        return lowPoints
            .map { basinSize(it, input, mutableSetOf(it)) }
            .sortedDescending()
            .take(3)
            .reduce { x, y -> x * y }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
