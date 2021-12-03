import java.util.Comparator

fun main() {

    fun gamma(input: List<String>): Int {
        return input.first()
            .mapIndexed { index, _ -> input.mostCommonCharAtIndex(index, ' ') }
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

    tailrec fun recursivelyFilterForCharAtIndex(
        list: List<String>,
        index: Int = 0,
        charSelector: (List<String>, Int) -> Char
    ): String {
        return if (list.size == 1)
            list[0]
        else
            recursivelyFilterForCharAtIndex(list.filter { it[index] == charSelector(list, index) }, index + 1, charSelector)

    }

    fun oxygenRating(input: List<String>): Int {
        return recursivelyFilterForCharAtIndex(input) { list, index ->
            list.mostCommonCharAtIndex(index, default = '1')
        }
            .toInt(2)
    }

    fun co2Rating(input: List<String>): Int {
        return recursivelyFilterForCharAtIndex(input) { list, index ->
            list.leastCommonCharAtIndex(index, default = '0')
        }
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

private fun List<String>.mostCommonCharAtIndex(index: Int, default: Char): Char {
    return map { it[index] }
        .groupingBy { it }
        .eachCount()
        .maxWithOrNull(
            Comparator.comparing<Map.Entry<Char, Int>?, Int?> { it.value }
                .thenComparing { (key, _) -> key == default }
        )?.key ?: default
}

private fun List<String>.leastCommonCharAtIndex(index: Int, default: Char): Char {
    return map { it[index] }
        .groupingBy { it }
        .eachCount()
        .minWithOrNull(
            Comparator.comparing<Map.Entry<Char, Int>?, Int?> { it.value }
                .thenComparing { (key, _) -> key != default }
        )?.key ?: default
}
