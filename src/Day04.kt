fun main() {

    fun bingoCardsFromInput(input: List<String>) = input.drop(1)
        .filter { it.isNotEmpty() }
        .chunked(5) { it.toBingoCard() }

    fun drawsFromInput(input: List<String>) = input[0].split(',').map { it.toInt() }

    fun part1(input: List<String>): Int {
        val draws = drawsFromInput(input)
        val bingoCards = bingoCardsFromInput(input)
        draws.forEach { draw ->
            bingoCards.forEach { it.mark(draw) }
            val winner = bingoCards.find { it.hasWon() }
            winner?.let { return it.sumOfUnmarked() * draw }
        }
        throw IllegalArgumentException("no winning card")
    }

    fun part2(input: List<String>): Int {
        val draws = drawsFromInput(input)
        val bingoCards = bingoCardsFromInput(input)
            .toMutableList()
        draws.forEach { draw ->
            bingoCards.forEach { it.mark(draw) }
            if (bingoCards.size == 1 && bingoCards[0].hasWon()) {
                return bingoCards[0].sumOfUnmarked() * draw
            }
            bingoCards.removeIf { it.hasWon() }
        }
        throw IllegalArgumentException("not all cards win")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private class BingoCard(private val rows: List<List<Cell>>) {

    data class Cell(val number: Int, var marked: Boolean = false)

    fun List<Cell>.isCompleted() = all { it.marked }

    fun mark(number: Int) {
        rows.flatten()
            .filter { it.number == number }
            .forEach { it.marked = true }
    }

    fun hasWon() = anyRowCompleted() || anyColumnCompleted()

    private fun anyRowCompleted() = rows.any { it.isCompleted() }

    private fun anyColumnCompleted(): Boolean {
        val columns = rows.first()
            .indices
            .map { index -> rows.map { it[index] } }
        return columns.any { it.isCompleted() }
    }

    fun sumOfUnmarked() = rows.flatten().filterNot { it.marked }.sumOf { it.number }
}

private fun List<String>.toBingoCard(): BingoCard {
    val rows = map { line ->
        line.trim()
            .split("\\s+".toRegex())
            .map { BingoCard.Cell(it.toInt()) }
    }
    return BingoCard(rows)
}
