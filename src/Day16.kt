fun main() {

    fun readPackets(reader: Reader, maxPackets: Int = Int.MAX_VALUE): List<Packet> {
        val result = mutableListOf<Packet>()
        while ((reader.remaining() > 7) && (result.size < maxPackets)) {
            val version = reader.next(3).toInt(2)
            val type = reader.next(3).toInt(2)
            val packet =
                if (type == 4) {
                    val number = buildString {
                        while (reader.nextChar() == '1') {
                            append(reader.next(4))
                        }
                        append(reader.next(4))
                    }
                    Literal(version, number.toLong(2))
                } else {
                    val subPackets =
                        if (reader.nextChar() == '0') {
                            val bitsInSubPackets = reader.next(15).toInt(2)
                            readPackets(reader.branch(bitsInSubPackets))
                        } else {
                            val numberOfSubPackets = reader.next(11).toInt(2)
                            readPackets(reader, maxPackets = numberOfSubPackets)
                        }
                    Operator(version, type, subPackets)
                }
            result.add(packet)
        }
        return result
    }

    fun String.hexToBinary() =
        map { it.digitToInt(16).toString(2).padStart(4, '0') }
            .joinToString(separator = "")

    fun part1(input: String): Int {
        val binaryInput = input.hexToBinary()
        val packets = readPackets(Reader(binaryInput))
        return packets.sumOf { it.versionSum() }
    }

    fun part2(input: String): Long {
        val binaryInput = input.hexToBinary()
        val packets = readPackets(Reader(binaryInput))
        return packets.single().resolve()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    testInput.takeWhile { it.isNotEmpty() }.forEach { testCase ->
        testCase.split(" ").let { check(part1(it[0]) == it[1].toInt()) }
    }
    testInput.takeLastWhile { it.isNotEmpty() }.forEach { testCase ->
        testCase.split(" ").let { check(part2(it[0]) == it[1].toLong()) }
    }

    val input = readInput("Day16").first()
    println(part1(input))
    println(part2(input))
}

private interface Packet {
    fun versionSum(): Int
    fun resolve(): Long
}

private data class Literal(val version: Int, val number: Long) : Packet {
    override fun versionSum() = version
    override fun resolve() = number
}

private data class Operator(val version: Int, val type: Int, val subPackets: List<Packet>) : Packet {

    override fun versionSum() = version + subPackets.sumOf { it.versionSum() }

    override fun resolve() = when (type) {
        0 -> subPackets.sumOf { it.resolve() }
        1 -> subPackets.map { it.resolve() }.fold(1L, Long::times)
        2 -> subPackets.minOf { it.resolve() }
        3 -> subPackets.maxOf { it.resolve() }
        5 -> if (subPackets[0].resolve() > subPackets[1].resolve()) 1 else 0
        6 -> if (subPackets[0].resolve() < subPackets[1].resolve()) 1 else 0
        7 -> if (subPackets[0].resolve() == subPackets[1].resolve()) 1 else 0
        else -> throw IllegalStateException()
    }
}

class Reader(private val string: String) {

    private var index: Int = 0

    fun branch(length: Int) = Reader(next(length))

    fun next(length: Int) = string.substring(index, index + length).also { index += length }

    fun nextChar() = string[index].also { index++ }

    fun remaining() = string.length - index

}