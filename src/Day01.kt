fun main() {
    fun Iterable<Int>.countIncrements() = windowed(2).count { it[1] > it[0] }

    fun part1(input: List<String>): Int {
        return input
            .map { it.toInt() }
            .countIncrements()
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toInt() }
            .windowed(3, transform = { it.sum() })
            .countIncrements()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
