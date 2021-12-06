fun main() {

    fun part1(input: List<String>): Long {
        val initialTimers = input.first().split(",").map { it.toInt() }
        val school = LanternfishSchool(initialTimers)
        repeat(80) { school.tick() }
        return school.size()
    }

    fun part2(input: List<String>): Long {
        val initialTimers = input.first().split(",").map { it.toInt() }
        val school = LanternfishSchool(initialTimers)
        repeat(256) { school.tick() }
        return school.size()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}


private class LanternfishSchool(initialTimers: List<Int>) {

    private val buckets: MutableList<Bucket> = mutableListOf()

    init {
        initialTimers.forEach { addFish(timer = it) }
    }

    data class Bucket(var timer: Int, var size: Long)

    fun tick() {
        var spawnCount: Long = 0
        buckets.forEach {
            if (it.timer > 0) {
                it.timer--
            } else {
                spawnCount += it.size
                it.timer = 6
            }
        }
        addFish(spawnCount, 8)
        spawnCount = 0
    }

    private fun addFish(count: Long = 1, timer: Int) {
        buckets.find { it.timer == timer }
            ?.let { it.size += count }
            ?: buckets.add(Bucket(timer, count))
    }

    fun size() = buckets.sumOf { it.size }
}
