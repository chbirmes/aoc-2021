fun main() {

    fun readCoordinates(input: List<String>) = input.takeWhile { it.isNotEmpty() }
        .map { it.split(",") }
        .map { it[0].toInt() to it[1].toInt() }

    fun readInstructions(input: List<String>) = input.takeLastWhile { it.isNotEmpty() }
        .map { it.split("=") }
        .map { it[0].last() to it[1].toInt() }

    fun part1(input: List<String>): Int {
        val coordinates = readCoordinates(input)
        val instruction = readInstructions(input).first()
        return coordinates.map { it.mirror(instruction) }
            .toSet()
            .size
    }

    fun part2(input: List<String>): String {
        val initialCoordinates = readCoordinates(input).toSet()
        val instructions = readInstructions(input)
        val finalCoordinates = instructions.fold(initialCoordinates) { coordinates, instruction ->
            coordinates.map { it.mirror(instruction) }.toSet()
        }

        val maxX = finalCoordinates.maxOf { it.first }
        val maxY = finalCoordinates.maxOf { it.second }
        val array = Array(maxY + 1) { BooleanArray(maxX + 1) }
        finalCoordinates.forEach { array[it.second][it.first] = true }
        return array.joinToString(separator = "\n") { line ->
            line.map { if (it) '#' else '.' }.joinToString(separator = "")
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
    check(
        part2(testInput) ==
                """
                |#####
                |#...#
                |#...#
                |#...#
                |#####
            """.trimMargin()
    )
    part2(testInput)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private typealias Coordinate = Pair<Int, Int>

private fun Coordinate.mirror(instruction: Instruction): Coordinate {
    return if (instruction.first == 'x')
        if (first < instruction.second) this
        else if (first == instruction.second) throw IllegalArgumentException()
        else copy(first = first - 2 * (first - instruction.second))
    else
        if (second < instruction.second) this
        else if (second == instruction.second) throw IllegalArgumentException()
        else copy(second = second - 2 * (second - instruction.second))
}

private typealias Instruction = Pair<Char, Int>