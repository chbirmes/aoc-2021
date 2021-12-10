import java.lang.IllegalArgumentException
import java.util.*

fun main() {

    fun scan(line: String): ScanResult {
        val expectedClosingBrackets = Stack<Char>()
        line.forEach { char ->
            if (char.isOpeningBracket()) {
                expectedClosingBrackets.push(char.correspondingClosingBracket())
            } else {
                if (char == expectedClosingBrackets.peek()) {
                    expectedClosingBrackets.pop()
                } else {
                    return ScanResult.Corrupted(char)
                }
            }
        }
        return ScanResult.Incomplete(expectedClosingBrackets)
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            when (val result = scan(it)) {
                is ScanResult.Corrupted -> result.offendingChar.offenceScore()
                is ScanResult.Incomplete -> 0
            }
        }
    }

    fun part2(input: List<String>): Long {
        return input.map { scan(it) }
            .filterIsInstance<ScanResult.Incomplete>()
            .map {
                it.expectedChars.foldRight(0L) { char, score ->
                    score * 5 + char.completionScore()
                }
            }
            .sorted()
            .let { it[(it.size - 1) / 2] }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

private fun Char.completionScore(): Long {
    return when (this) {
        ')' -> 1
        ']' -> 2
        '}' -> 3
        '>' -> 4
        else -> throw IllegalArgumentException()
    }
}

private fun Char.offenceScore(): Int {
    return when (this) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> throw IllegalArgumentException()
    }
}

private fun Char.correspondingClosingBracket(): Char {
    return when (this) {
        '(' -> ')'
        '[' -> ']'
        '{' -> '}'
        '<' -> '>'
        else -> throw IllegalArgumentException()
    }
}

private fun Char.isOpeningBracket(): Boolean {
    return this in setOf('(', '[', '{', '<')
}

private sealed class ScanResult {
    data class Corrupted(val offendingChar: Char) : ScanResult()
    data class Incomplete(val expectedChars: Stack<Char>) : ScanResult()
}
