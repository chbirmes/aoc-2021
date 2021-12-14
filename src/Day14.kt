fun main() {

    fun polymerize(input: List<String>, repetitions:Int): Long {
        val template = input.first()
        val rules = input.drop(2).map { parseRule(it) }
        val initialCounts = pairCountsOf(template)
        val finalCounts = 1.rangeTo(repetitions).fold(initialCounts) { counts, _ -> counts.apply(rules) }
        val charFrequencies = finalCounts.firstCharFrequencies().addOne(template.last())
        return charFrequencies.maxOf { it.value } - charFrequencies.minOf { it.value }
    }

    fun part1(input: List<String>) = polymerize(input, 10)

    fun part2(input: List<String>) = polymerize(input, 40)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private typealias CharPair = Pair<Char, Char>

private class Rule(val input: CharPair, charToInsert: Char) {
    val output = CharPair(input.first, charToInsert) to CharPair(charToInsert, input.second)
}

private fun parseRule(line: String) = line.split(" -> ")
    .let { Rule(CharPair(it[0][0], it[0][1]), it[1].single()) }

private class PairCounts(private val counterMap: Map<CharPair, Long>) {

    operator fun plus(other: PairCounts) = PairCounts(
        (counterMap.keys + other.counterMap.keys).associateWith {
            (counterMap[it] ?: 0) + (other.counterMap[it] ?: 0)
        }
    )

    fun apply(rules: List<Rule>): PairCounts {
        val ruleInputs = rules.map { it.input }.toSet()
        val (toProcess, toKeep) = counterMap.entries.partition { it.key in ruleInputs }
        val newCounts = toProcess.map { (pair, count) ->
            val ruleOutput = rules.single { it.input == pair }.output
            PairCounts(mapOf(ruleOutput.first to count, ruleOutput.second to count))
        }
        return newCounts.fold(PairCounts(toKeep.toMap()), PairCounts::plus)
    }

    fun firstCharFrequencies() = counterMap.keys.map { it.first }.toSet()
        .associateWith { char ->
            counterMap.filterKeys { it.first == char }.values.sum()
        }

}

private fun pairCountsOf(template: String) = template.windowed(2)
    .map { PairCounts(mapOf(CharPair(it[0], it[1]) to 1)) }
    .reduce(PairCounts::plus)

private fun Map<Char, Long>.addOne(char: Char) = filterKeys { it != char } + (char to getOrDefault(char, 0) + 1)

private fun <K, V> List<Map.Entry<K, V>>.toMap() = associate { it.key to it.value }