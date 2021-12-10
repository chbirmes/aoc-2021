fun main() {

    fun part1(input: List<String>): Int {
        val pairs = input.map { it.split(" | ") }
            .map { it[0].split(" ") to it[1].split(" ") }
        return pairs.sumOf { it.second.count { code -> code.length in setOf(2, 4, 3, 7) } }
    }

    fun part2(input: List<String>) = input
        .map { it.split(" | ") }
        .map { it[0].split(" ") to it[1].split(" ") }
        .sumOf { (allDigits, wantedDigits) ->
            val unmapped = allDigits.map { it.toSet() }.toMutableSet()
            val mapping = mutableMapOf<Char, Set<Char>>()

            fun Set<Char>.mapTo(digit: Char) {
                mapping[digit] = this
                unmapped.remove(this)
            }

            unmapped.single { it.size == 2 }.mapTo('1')
            unmapped.single { it.size == 3 }.mapTo('7')
            unmapped.single { it.size == 4 }.mapTo('4')
            unmapped.single { it.size == 7 }.mapTo('8')
            unmapped.single { it.size == 6 && it.containsAll(mapping['4']!!) }.mapTo('9')
            unmapped.single { it.size == 6 && it.containsAll(mapping['1']!!) }.mapTo('0')
            unmapped.single { it.size == 6 }.mapTo('6')
            unmapped.single { it.containsAll(mapping['1']!!) }.mapTo('3')
            unmapped.single { mapping['6']!!.containsAll(it) }.mapTo('5')
            unmapped.single().mapTo('2')

            val reverseMapping = mapping.entries.associate { (code, digit) -> digit to code }
            wantedDigits.map { reverseMapping[it.toSet()] }
                .joinToString(separator = "")
                .toInt()
        }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
