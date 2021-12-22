import kotlin.math.max
import kotlin.math.min

fun main() {

    fun readSteps(input: List<String>): List<Pair<Cuboid, Boolean>> =
        input.map { line -> line.split(" ") }
            .map { (on, ranges) ->
                ranges.substringAfter('=').split("..", ",y=", ",z=").map { it.toInt() }
                    .let {
                        Cuboid(
                            IntRange(it[0], it[1]),
                            IntRange(it[2], it[3]),
                            IntRange(it[4], it[5])
                        )
                    } to (on == "on")
            }

    fun part1(input: List<String>): Int {
        val steps = readSteps(input).map { (cuboid, on) -> cuboid.trim() to on }
        val litCubes = mutableSetOf<Cube>()
        steps.forEach { (cuboid, turnOn) ->
            if (turnOn) {
                litCubes.addAll(cuboid.cubes())
            } else {
                litCubes.removeAll(cuboid.cubes().toSet())
            }
        }
        return litCubes.size
    }

    fun part2(input: List<String>): Long {
        val steps = readSteps(input)
        var litCuboids = setOf(steps.first().first)
        steps.drop(1).forEach { (next, on) ->
            litCuboids = if (on) {
                litCuboids.flatMap { it.union(next) }.toSet()
            } else {
                litCuboids.flatMap { it.difference(next) }.toSet()
            }
        }
        return litCuboids.sumOf { it.volume() }
    }

    check(part1(readInput("Day22_test")) == 590784)
    check(part2(readInput("Day22_2_test")) == 2758514936282235)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}

private data class Cube(val x: Int, val y: Int, val z: Int)

private data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {

    private fun IntRange.trim() = IntRange(max(first, -50), min(last, 50))

    private fun IntRange.size() = (last - first + 1).toLong()

    private fun IntRange.overlap(other: IntRange): IntRange? =
        if (other.first <= first && last <= other.last)
            this
        else if (first <= other.first && other.last <= last)
            other
        else if (other.first <= first && first <= other.last)
            IntRange(first, other.last)
        else if (other.first in this)
            IntRange(other.first, last)
        else
            null

    private fun IntRange.split(other: IntRange): Set<IntRange> =
        if (other.first <= first && last <= other.last)
            setOf(this)
        else if (first < other.first && other.last < last)
            setOf(
                IntRange(first, other.first - 1),
                IntRange(other.first, other.last),
                IntRange(other.last + 1, last)
            )
        else if (other.last in first until last)
            setOf(IntRange(first, other.last), IntRange(other.last + 1, last))
        else if (other.first in (first + 1)..last)
            setOf(
                IntRange(first, other.first - 1),
                IntRange(other.first, other.last)
            )
        else
            setOf()

    fun trim() = Cuboid(xRange.trim(), yRange.trim(), zRange.trim())

    fun cubes() = xRange.flatMap { x -> yRange.flatMap { y -> zRange.map { z -> Cube(x, y, z) } } }

    fun volume(): Long = xRange.size() * yRange.size() * zRange.size()

    private fun overlap(other: Cuboid) =
        xRange.overlap(other.xRange)?.let { x ->
            yRange.overlap(other.yRange)?.let { y ->
                zRange.overlap(other.zRange)?.let { z ->
                    Cuboid(x, y, z)
                }
            }
        }

    private fun split(overlap: Cuboid): Set<Cuboid> =
        xRange.split(overlap.xRange).flatMap { x ->
            yRange.split(overlap.yRange).flatMap { y ->
                zRange.split(overlap.zRange).map { z ->
                    Cuboid(x, y, z)
                }
            }
        }
            .toSet()

    fun union(other: Cuboid): Set<Cuboid> {
        val overlap = overlap(other)
        return if (overlap == null) {
            setOf(this, other)
        } else {
            buildSet {
                addAll(split(overlap))
                add(other)
                remove(overlap)
            }
        }
    }

    fun difference(other: Cuboid): Set<Cuboid> {
        val overlap = overlap(other)
        return if (overlap == null) {
            setOf(this)
        } else {
            buildSet {
                addAll(split(overlap))
                remove(overlap)
            }
        }
    }

}
