fun main() {

    fun applyRules(s: String, rules: Map<String, Char>): String {
        val inserts = s.windowed(2)
            .map { rules[it] ?: '#' }
            .joinToString(separator = "")
        val list = s.zip(inserts) { a, b -> a.toString() + b.toString() } + s.last().toString()
        return list.joinToString(separator = "")
            .filterNot { it == '#' }
    }

    fun part1(input: List<String>): Int {
        val template = input.first()
        val rules = input.drop(2)
            .map { it.split(" -> ") }
            .associate { it[0] to it[1].single() }
        val finalString = 1.rangeTo(10).fold(template) { acc, _ -> applyRules(acc, rules) }
        val frequencies = finalString.groupingBy { it }.eachCount().map { it.value }
        return frequencies.maxOf { it } - frequencies.minOf { it }
    }

    fun applyBucketRules(pairBuckets: PairBuckets, rules: Map<Pair<Char, Char>, Char>) {
        val newPairBuckets = PairBuckets()
        rules.forEach { (pair, newChar) ->
            val count = pairBuckets.countOf(pair)
            if (count > 0) {
                newPairBuckets.add(pair.first to newChar, count)
                newPairBuckets.add(newChar to pair.second, count)
                pairBuckets.remove(pair)
            }
        }
        pairBuckets.addBuckets(newPairBuckets)
    }

    fun part2(input: List<String>): Long {
        val template = input.first()
        val rules = input.drop(2)
            .map { it.split(" -> ") }
            .associate { it[0].let { lhs -> lhs[0] to lhs[1] } to it[1].single() }

        val pairBuckets = PairBuckets()
        template.windowed(2).forEach { pairBuckets.add(it[0] to it[1], 1) }

        repeat(40) { applyBucketRules(pairBuckets, rules) }

        val alphabet = pairBuckets.counterMap.keys
            .flatMap { setOf(it.first, it.second) }
            .toSet()
        val charFrequencies = alphabet.associateWith { char ->
            pairBuckets.counterMap.filterKeys { it.first == char }
                .values.sum() + if (char == template.last()) 1 else 0
        }
        return charFrequencies.maxOf { it.value } - charFrequencies.minOf { it.value }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private data class PairBuckets(val counterMap: MutableMap<Pair<Char, Char>, Long> = mutableMapOf()) {

    fun add(pair: Pair<Char, Char>, count: Long) {
        counterMap[pair] = countOf(pair) + count
    }

    fun countOf(pair: Pair<Char, Char>) = counterMap.getOrDefault(pair, 0)

    fun remove(pair: Pair<Char, Char>) {
        counterMap.remove(pair)
    }

    fun addBuckets(newPairBuckets: PairBuckets) {
        newPairBuckets.counterMap.forEach { (pair, count) ->
            add(pair, count)
        }
    }
}
