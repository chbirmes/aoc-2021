fun main() {

    fun part1(input: List<String>): Int {
        val grid = OctopusGrid(input)
        repeat(100) { grid.tick() }
        return grid.flashCounter
    }

    fun part2(input: List<String>): Int {
        val grid = OctopusGrid(input)
        var counter = 0
        while (!grid.allFlashed) {
            grid.tick()
            counter += 1
        }
        return counter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private class OctopusGrid(initialConfiguration: List<String>) {

    var allFlashed = false
    var flashCounter: Int = 0

    private val octopuses = initialConfiguration.mapIndexed { x, row ->
        row.mapIndexed { y, char -> Octopus(Position(x, y), char.digitToInt(), this) }
    }

    private class Octopus(val position: Position, var energy: Int, val grid: OctopusGrid) {
        var flashed = false

        fun reset() {
            flashed = false
            energy = 0
        }

        fun flash() {
            if (!flashed) {
                flashed = true
                grid.flashCounter += 1
                nonFlashedNeighbors().forEach {
                    it.energy += 1
                    if (it.energy > 9) {
                        it.flash()
                    }
                }
            }
        }

        private fun nonFlashedNeighbors(): List<Octopus> {
            return listOf(
                Position(position.x - 1, position.y - 1),
                Position(position.x, position.y - 1),
                Position(position.x + 1, position.y - 1),
                Position(position.x - 1, position.y),
                Position(position.x + 1, position.y),
                Position(position.x - 1, position.y + 1),
                Position(position.x, position.y + 1),
                Position(position.x + 1, position.y + 1)
            )
                .filter { (x, y) -> x in grid.octopuses.indices && y in grid.octopuses.first().indices }
                .map { (x, y) -> grid.octopuses[x][y] }
                .filter { !it.flashed }
        }

    }

    fun tick() {
        val flatOctopusList = octopuses.flatten()
        flatOctopusList.forEach { it.energy += 1 }
        flatOctopusList.filter { it.energy > 9 }
            .forEach { it.flash() }
        allFlashed = flatOctopusList.all { it.flashed }
        flatOctopusList.filter { it.flashed }
            .forEach { it.reset() }

    }

    private data class Position(val x: Int, val y: Int)

}
