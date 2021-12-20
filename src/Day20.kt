fun main() {

    fun enhance(original: Array<BooleanArray>, algorithm: String, stepCount: Int): Array<BooleanArray> {
        val default = if (stepCount % 2 == 1)
            algorithm[0] == '#'
        else
            algorithm[0] == '#' && algorithm.last() != '.'

        val result = Array(original.size + 2) { BooleanArray(original.first().size + 2) { default } }
        for (x in 1 until original.size - 1) {
            for (y in 1 until original[0].size - 1) {
                val index = listOf(
                    original[x - 1][y - 1],
                    original[x - 1][y],
                    original[x - 1][y + 1],
                    original[x][y - 1],
                    original[x][y],
                    original[x][y + 1],
                    original[x + 1][y - 1],
                    original[x + 1][y],
                    original[x + 1][y + 1]
                ).map { if (it) '1' else '0' }.joinToString("").toInt(2)
                result[x + 1][y + 1] = algorithm[index] == '#'
            }
        }
        return result
    }

    fun readOriginal(input: List<String>): Array<BooleanArray> {
        val originalStrings = input.drop(2)
        val original = Array(originalStrings.size + 4) { BooleanArray(originalStrings.first().length + 4) }
        originalStrings.forEachIndexed { x, line ->
            line.forEachIndexed { y, char -> original[x + 2][y + 2] = char == '#' }
        }
        return original
    }

    fun part1(input: List<String>): Int {
        val original = readOriginal(input)
        val enhanced = 1.rangeTo(2).fold(original) { image, step -> enhance(image, input.first(), step) }
        return enhanced.sumOf { line -> line.count { it } }
    }

    fun part2(input: List<String>): Int {
        val original = readOriginal(input)
        val enhanced = 1.rangeTo(50).fold(original) { image, step -> enhance(image, input.first(), step) }
        return enhanced.sumOf { line -> line.count { it } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
