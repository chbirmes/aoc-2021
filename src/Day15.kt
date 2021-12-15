fun main() {

    fun dijkstra(costMap: Array<IntArray>): Int {
        val target = costMap.size - 1 to costMap.size - 1
        val open = mutableMapOf((0 to 0) to 0)
        val visited = mutableSetOf<Pair<Int, Int>>()
        while (open.isNotEmpty()) {
            val current = open.entries.minByOrNull { it.value }!!
            open.remove(current.key)
            if (current.key == target) {
                return current.value
            }
            visited.add(current.key)
            current.key.let {
                sequenceOf(
                    it.first - 1 to it.second,
                    it.first to it.second - 1,
                    it.first + 1 to it.second,
                    it.first to it.second + 1
                )
                    .filter { neighbor -> neighbor.first in costMap.indices && neighbor.second in costMap.indices }
                    .filterNot { neighbor -> neighbor in visited }
            }
                .forEach {
                    val totalCost = current.value + costMap[it.first][it.second]
                    if (totalCost < open.getOrDefault(it, Int.MAX_VALUE)) {
                        open[it] = totalCost
                    }
                }
        }
        throw IllegalStateException()
    }

    fun part1(input: List<String>): Int {
        val costMap = Array(input.size) { IntArray(input.size) }
        for (x in input.indices) {
            for (y in input.indices) {
                costMap[x][y] = input[x][y].digitToInt()
            }
        }
        return dijkstra(costMap)
    }

    fun part2(input: List<String>): Int {
        val size = input.size
        val costMap = Array(input.size * 5) { IntArray(input.size * 5) }
        for (x in 0 until size) {
            for (y in 0 until size) {
                for (dx in 0..4) {
                    for (dy in 0..4) {
                        costMap[dx * size + x][dy * size + y] = ((input[x][y].digitToInt() + dx + dy - 1) % 9) + 1
                    }
                }
            }
        }
        return dijkstra(costMap)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
//    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
