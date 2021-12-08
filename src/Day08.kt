fun main() {

    fun part1(input: List<String>): Int {
        val pairs = input.map { it.split(" | ") }
            .map { it[0].split(" ") to it[1].split(" ") }
        return pairs.sumOf { it.second.count { code -> code.length in setOf(2, 4, 3, 7) } }
    }

    val alphabet = "abcdefg"
    val digitCodes = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")

    fun decode(encodedDigits: List<String>, segmentTranslation: Map<Char, Char>): Int {
        val translated = encodedDigits.map { digit ->
            digit.map { segmentTranslation[it]!! }
                .sorted()
                .joinToString(separator = "")
        }
        return translated.joinToString(separator = "") { digitCodes.indexOf(it).toString() }
            .toInt()
    }

    // find possible ENCODED segments for a DECODED segment parameter
    fun calculateSegmentCandidates(segment: Char, digitCandidates: List<List<String>>): String {
        val digitCodeMap = digitCodes.mapIndexed { index, s -> index to s }
        val segmentContainingDigits = digitCodeMap
            .filter { it.second.contains(segment) }
            .map { it.first }
        // an ENCODED segment must occur in at least one candidate for each digit that the DECODED segment occurs in
        // also, it must NOT occur in at least one candidate for each digit that the DECODED segment does NOT occur in
        val containingCandidates = digitCandidates.filterIndexed { index, _ -> index in segmentContainingDigits }
        val notContainingCandidates = digitCandidates.filterIndexed { index, _ -> index !in segmentContainingDigits }
        return alphabet.filter { char ->
            containingCandidates.all { candidates ->
                candidates.any { it.contains(char) }
            } && notContainingCandidates.all { candidates ->
                candidates.any { !it.contains(char) }
            }
        }
    }

    fun calculateSegmentTranslation(cues: List<String>): Map<Char, Char> {
        val digitCandidates = digitCodes.map { code -> cues.filter { it.length == code.length } }
        var segmentCandidates = alphabet.map { it to calculateSegmentCandidates(it, digitCandidates) }
        while (!segmentCandidates.all { it.second.length == 1 }) {
            val taken = segmentCandidates.map { it.second }.filter { it.length == 1 }.map { it.first() }
            segmentCandidates = segmentCandidates.map {
                if (it.second.length == 1)
                    it
                else
                    it.first to it.second.filter { s -> s !in taken }
            }
        }
        return segmentCandidates.associate { it.second.first() to it.first }
    }

    fun part2(input: List<String>): Int {
        val cuesToWanted = input
            .map { it.split(" | ") }
            .map { it[0].split(" ") to it[1].split(" ") }
        return cuesToWanted.sumOf {
            val segmentTranslation = calculateSegmentTranslation(it.first)
            decode(it.second, segmentTranslation)
        }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
