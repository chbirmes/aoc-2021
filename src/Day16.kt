fun main() {

    fun readPackets(cursor: Cursor, maxPackets: Int = Int.MAX_VALUE): List<Packet> {
        val result = mutableListOf<Packet>()
        while ((cursor.remaining() > 7) && (result.size < maxPackets)) {
            val version = cursor.next(3).toInt(2)
            val type = cursor.next(3).toInt(2)
            val packet =
                if (type == 4) {
                    val number = cursor.nextLiteralGroups()
                        .joinToString(separator = "") { it.substring(1) }
                        .toLong(2)
                    Literal(version, number)
                } else {
                    val subPackets =
                        if (cursor.nextChar() == '0') {
                            val bitsInSubPackets = cursor.next(15).toInt(2)
                            readPackets(cursor.branch(bitsInSubPackets))
                        } else {
                            val numberOfSubPackets = cursor.next(11).toInt(2)
                            readPackets(cursor, maxPackets = numberOfSubPackets)
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

    fun part1(input: List<String>): Int {
        val binaryInput = input.first().hexToBinary()
        val packets = readPackets(Cursor(binaryInput))
        return packets.sumOf { it.versionSum() }
    }

    fun part2(input: List<String>): Long {
        val binaryInput = input.first().hexToBinary()
        val packets = readPackets(Cursor(binaryInput))
        return packets.single().resolve()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
//    check(part1(testInput) == 31)
    check(part2(testInput) == 1L)

    val input = readInput("Day16")
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

data class Cursor(val string: String, private var index: Int = 0) {

    fun branch(length: Int) = Cursor(next(length))

    fun next(length: Int) = string.substring(index, index + length).also { index += length }

    fun nextChar() = string[index].also { index++ }

    fun remaining() = string.length - index

    fun nextLiteralGroups(): List<String> {
        val headGroups = string.substring(index).chunked(5).takeWhile { it.first() == '1' }
        val headGroupsLength = headGroups.size * 5
        val tailGroup = string.substring(index + headGroupsLength, index + 5 + headGroupsLength)
        val allGroups = headGroups + tailGroup
        index += allGroups.size * 5
        return allGroups
    }

}
