import kotlin.math.abs
import kotlin.math.min

fun main() {

    fun readScanners(input: List<String>): List<Scanner> {
        var lines = input.filter { it.isNotEmpty() }
        val scanners = buildList {
            while (lines.isNotEmpty()) {
                lines.drop(1).takeWhile { !it.startsWith("---") }
                    .map { line -> line.split(",").map { it.toInt() } }
                    .map { P(it[0], it[1], it[2]) }
                    .let { add(Scanner(it)) }
                lines = lines.drop(1).dropWhile { !it.startsWith("---") }
            }
        }
        return scanners
    }

    fun alignScanners(scanners: List<Scanner>): List<Scanner> {
        val open = scanners.toMutableList()
        val finished = mutableListOf<Scanner>()
        val ongoing = mutableListOf(open.first().also { open.remove(it) })
        while (open.isNotEmpty()) {
            val reference = ongoing.first()
            val alignMap = open.associateWith { reference.align(it) }
            alignMap.forEach { (unaligned, aligned) ->
                if (aligned != null) {
                    ongoing.add(aligned)
                    open.remove(unaligned)
                }
            }
            finished.add(reference)
            ongoing.remove(reference)
        }
        finished.addAll(ongoing)
        return finished
    }

    fun part1(input: List<String>): Int {
        val scanners = readScanners(input)
        val alignedScanners = alignScanners(scanners)
        return buildSet { alignedScanners.forEach { addAll(it.beacons) } }.size
    }

    fun part2(input: List<String>): Int {
        val scanners = readScanners(input)
        val alignedScanners = alignScanners(scanners)
        return alignedScanners.indices.flatMap { a ->
            alignedScanners.indices.filter { b -> b > a }
                .map { b -> alignedScanners[a].originalOffset to alignedScanners[b].originalOffset }
        }
            .maxOf { (a, b) -> (a - b).manhattanLength() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

private data class P(val x: Int, val y: Int, val z: Int) {
    operator fun minus(other: P) = P(x - other.x, y - other.y, z - other.z)
    fun manhattanLength() = abs(x) + abs(y) + abs(z)
}

private typealias Orientation = (P) -> P

private val identity: Orientation = { it }

private val orientations = sequenceOf(
    identity,
    { (x, y, z) -> P(-x, -y, z) },
    { (x, y, z) -> P(-x, y, -z) },
    { (x, y, z) -> P(x, -y, -z) },

    { (x, y, z) -> P(-y, x, z) },
    { (x, y, z) -> P(y, -x, z) },
    { (x, y, z) -> P(y, x, -z) },
    { (x, y, z) -> P(-y, -x, -z) },

    { (x, y, z) -> P(-z, y, x) },
    { (x, y, z) -> P(z, -y, x) },
    { (x, y, z) -> P(z, y, -x) },
    { (x, y, z) -> P(-z, -y, -x) },

    { (x, y, z) -> P(-x, z, y) },
    { (x, y, z) -> P(x, -z, y) },
    { (x, y, z) -> P(x, z, -y) },
    { (x, y, z) -> P(-x, -z, -y) },

    { (x, y, z) -> P(y, z, x) },
    { (x, y, z) -> P(-y, -z, x) },
    { (x, y, z) -> P(-y, z, -x) },
    { (x, y, z) -> P(y, -z, -x) },

    { (x, y, z) -> P(z, x, y) },
    { (x, y, z) -> P(-z, -x, y) },
    { (x, y, z) -> P(-z, x, -y) },
    { (x, y, z) -> P(z, -x, -y) }
)

private class Scanner(val beacons: List<P>, val originalOffset: P = P(0, 0, 0)) {

    private val relativeDistances = beacons.associateWith { beacon ->
        (beacons - beacon).map { (it - beacon).manhattanLength() }
    }
    private val orientedRelativePositions = LazyOrientedRelativePositionsMap(beacons)

    fun align(other: Scanner): Scanner? {
        for (beacon in beacons) {
            for (otherBeacon in other.beacons) {
                if (commonElementCount(relativeDistances[beacon]!!, other.relativeDistances[otherBeacon]!!) >= 11) {
                    for (orientation in orientations) {
                        val oriented = other.orientedRelativePositions.get(orientation, otherBeacon)
                        if (orientedRelativePositions.get(identity, beacon).intersect(oriented).size >= 11) {
                            val offset = orientation.invoke(otherBeacon) - beacon
                            val newBeacons = other.beacons.map { orientation.invoke(it) - offset }
                            return Scanner(newBeacons, offset)
                        }
                    }
                }
            }
        }
        return null
    }

}

private class LazyOrientedRelativePositionsMap(private val beacons: List<P>) {

    val backingMap: MutableMap<Orientation, MutableMap<P, Set<P>>> = mutableMapOf()

    fun get(orientation: Orientation, beacon: P): Set<P> =
        backingMap.computeIfAbsent(orientation) { mutableMapOf() }
            .computeIfAbsent(beacon) {
                if (orientation == identity)
                    (beacons - it).map { p -> p - beacon }.toSet()
                else
                    get(identity, it).map { p -> orientation.invoke(p) }.toSet()
            }

}

private fun <T> commonElementCount(a: List<T>, b: List<T>) =
    a.intersect(b.toSet()).sumOf { x -> min(a.count { it == x }, b.count { it == x }) }