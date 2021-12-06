fun main() {

    fun gamma(input: List<String>): Int {
        return input.first()
            .indices
            .map { input.mostCommonCharAt(it) }
            .joinToString(separator = "")
            .toInt(2)
    }

    fun epsilon(gamma: Int, bitCount: Int): Int {
        return gamma.inv() and ((1 shl bitCount) - 1)
    }

    fun part1(input: List<String>): Int {
        val gamma = gamma(input)
        val epsilon = epsilon(gamma, input.first().length)
        return gamma * epsilon
    }

    tailrec fun recursivelyFilterForChar(
        list: List<String>,
        index: Int = 0,
        charSelector: (List<String>, Int) -> Char
    ): String {
        return if (list.size == 1)
            list[0]
        else {
            val filtered = list.filter { it[index] == charSelector(list, index) }
            recursivelyFilterForChar(filtered, index + 1, charSelector)
        }

    }

    fun oxygenRating(input: List<String>): Int {
        return recursivelyFilterForChar(input) { list, index -> list.mostCommonCharAt(index, bias = '1') }
            .toInt(2)
    }

    fun co2Rating(input: List<String>): Int {
        return recursivelyFilterForChar(input) { list, index -> list.leastCommonCharAt(index, bias = '0') }
            .toInt(2)
    }

    fun part2(input: List<String>): Int {
        val oxygenRating = oxygenRating(input)
        val co2Rating = co2Rating(input)
        return oxygenRating * co2Rating
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.mostCommonCharAt(index: Int, bias: Char): Char {
    return mostCommonCharAt(index) { bias }
}

private fun List<String>.mostCommonCharAt(
    index: Int,
    bias: () -> Char = { throw IllegalArgumentException("unique maximum required") }
): Char {
    val charCounts = charCounts(index)
    val maxCount = charCounts.maxOf { it.value }
    return charCounts.filter { it.value == maxCount }
        .let { if (it.size == 1) it.entries.first().key else bias() }
}

private fun List<String>.leastCommonCharAt(index: Int, bias: Char): Char {
    val charCounts = charCounts(index)
    val minCount = charCounts.minOf { it.value }
    return charCounts.filter { it.value == minCount }
        .let { if (it.size == 1) it.entries.first().key else bias }
}

private fun List<String>.charCounts(index: Int) = map { it[index] }
    .groupingBy { it }
    .eachCount()
