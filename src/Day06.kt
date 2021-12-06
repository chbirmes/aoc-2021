fun main() {

    fun part1(input: List<String>): Long {
        val school = LanternfishSchool()
        input.first().split(",")
            .forEach { school.addFish(1, it.toInt()) }
        repeat(80) { school.tick() }
        return school.size()
    }

    fun part2(input: List<String>): Long {
        val school = LanternfishSchool()
        input.first().split(",")
            .forEach { school.addFish(1, it.toInt()) }
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


class LanternfishSchool {

    data class Bucket(var timer: Int, var size: Long)

    private val buckets: MutableList<Bucket> = mutableListOf()
    private var spawnCount: Long = 0

    fun tick() {
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

    fun addFish(count: Long, timer: Int) {
        buckets.find { it.timer == timer }
            ?.let { it.size += count }
            ?: buckets.add(Bucket(timer, count))
    }

    fun size() = buckets.sumOf { it.size }
}
