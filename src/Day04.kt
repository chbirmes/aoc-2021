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
            winner?.let { return it.sumOfUnchecked() * draw }
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
                return bingoCards[0].sumOfUnchecked() * draw
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

class BingoCard(private val rows: List<List<Cell>>) {

    data class Cell(val number: Int, var checked: Boolean = false)

    fun mark(number: Int) {
        rows.flatten()
            .filter { it.number == number }
            .forEach { it.checked = true }
    }

    fun hasWon() = anyRowCompleted() || anyColumnCompleted()

    private fun anyRowCompleted() = rows.any { row -> row.all { it.checked } }

    private fun anyColumnCompleted(): Boolean {
        val columns = rows.first().mapIndexed { index, _ -> rows.map { it[index] } }
        return columns.any { column -> column.all { it.checked } }
    }

    fun sumOfUnchecked() = rows.flatten().filterNot { it.checked }.sumOf { it.number }
}

fun List<String>.toBingoCard(): BingoCard {
    val rows = map { line ->
        line.trim()
            .split("\\s+".toRegex())
            .map { BingoCard.Cell(it.toInt()) }
    }
    return BingoCard(rows)
}
