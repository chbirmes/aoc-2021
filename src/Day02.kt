fun main() {

    data class Command(val direction: String, val units: Int)

    data class Position(val horizontal: Int, val depth: Int) {

        fun executeCommand(command: Command) =
            when (command.direction) {
                "forward" -> copy(horizontal = horizontal + command.units)
                "down" -> copy(depth = depth + command.units)
                "up" -> copy(depth = depth - command.units)
                else -> this
            }

    }

    fun Command(string: String) = string.split(' ', limit = 2).let { Command(it[0], it[1].toInt()) }

    fun part1(input: List<String>) = input
        .map { Command(it) }
        .fold(Position(0, 0), Position::executeCommand)
        .let { it.horizontal * it.depth }


    data class PositionWithAim(val horizontal: Int, val depth: Int, val aim: Int) {

        fun executeCommand(command: Command) =
            when (command.direction) {
                "forward" -> copy(
                    horizontal = horizontal + command.units,
                    depth = depth + aim * command.units
                )
                "down" -> copy(aim = aim + command.units)
                "up" -> copy(aim = aim - command.units)
                else -> this
            }

    }

    fun part2(input: List<String>) = input
        .map { Command(it) }
        .fold(PositionWithAim(0, 0, 0), PositionWithAim::executeCommand)
        .let { it.horizontal * it.depth }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
