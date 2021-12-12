fun main() {

    fun part1(input: List<String>) = Maze(input).countPathsFromStartToEnd(SmallCaveVisitationPolicy.OnceAtMost())

    fun part2(input: List<String>) = Maze(input).countPathsFromStartToEnd(SmallCaveVisitationPolicy.OneCaveTwice())

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 226)
    check(part2(testInput) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private class Maze(input: List<String>) {

    val connections = input
        .map { line -> line.split("-") }
        .map { it[0] to it[1] }

    private fun destinationsFrom(cave: String) =
        connections.filter { it.first == cave }.map { it.second } +
                connections.filter { it.second == cave }.map { it.first }

    fun countPathsFromStartToEnd(policy: SmallCaveVisitationPolicy) = countPathsToEnd(listOf("start" to policy))

    private tailrec fun countPathsToEnd(paths: List<Pair<String, SmallCaveVisitationPolicy>>): Int {
        val (finished, ongoing) = paths.partition { it.first == "end" }
        return if (ongoing.isEmpty()) {
            finished.size
        } else {
            val nextPaths = ongoing.flatMap { (from, previousVisits) ->
                val visits = previousVisits + from
                val next = destinationsFrom(from).filter { visits.allowsVisit(it) }
                next.map { it to visits }
            }
            countPathsToEnd(nextPaths + finished)
        }
    }

}

private interface SmallCaveVisitationPolicy {

    operator fun plus(cave: String): SmallCaveVisitationPolicy
    fun allowsVisit(cave: String): Boolean

    data class OnceAtMost(val visits: Set<String> = setOf()) : SmallCaveVisitationPolicy {
        override fun allowsVisit(cave: String) = cave !in visits
        override fun plus(cave: String) = if (cave.first().isLowerCase()) copy(visits = visits + cave) else this
    }

    data class OneCaveTwice(val visited: Set<String> = emptySet(), val oneVisitedTwice: Boolean = false) :
        SmallCaveVisitationPolicy {

        override fun allowsVisit(cave: String) = cave != "start" && (cave !in visited || !oneVisitedTwice)

        override operator fun plus(cave: String) =
            if (cave.first().isLowerCase())
                if (cave !in visited) copy(visited = visited + cave)
                else if (!oneVisitedTwice) copy(oneVisitedTwice = true)
                else throw IllegalStateException()
            else this
    }

}


