fun main() {

    fun readPackets(binaryInput: String, maxPackets: Int = Int.MAX_VALUE, cursor: Cursor = Cursor(0)): List<Packet> {
        val result = mutableListOf<Packet>()
        while ((cursor.index + 7 < binaryInput.length) && (result.size < maxPackets)) {
            val version = binaryInput.substring(cursor.index, cursor.index + 3).toInt(2)
            cursor.index += 3

            val type = binaryInput.substring(cursor.index, cursor.index + 3).toInt(2)
            cursor.index += 3

            val packet = if (type == 4) Literal(version) else Operator(version, type)
            when (packet) {
                is Literal -> {
                    val headGroups = binaryInput.substring(cursor.index).chunked(5).takeWhile { it.first() == '1' }
                    val headGroupsLength = headGroups.size * 5
                    val tailGroup =
                        binaryInput.substring(cursor.index + headGroupsLength, cursor.index + 5 + headGroupsLength)
                    val allGroups = headGroups + tailGroup
                    packet.number = allGroups.joinToString(separator = "") { it.substring(1) }
                        .toLong(2)
                    cursor.index += allGroups.size * 5
                }
                is Operator -> {
                    cursor.index++
                    if (binaryInput[cursor.index - 1] == '0') {
                        val bitsInSubPackets = binaryInput.substring(cursor.index, cursor.index + 15).toInt(2)
                        cursor.index += 15
                        packet.subPackets =
                            readPackets(binaryInput.substring(cursor.index, cursor.index + bitsInSubPackets))
                        cursor.index += bitsInSubPackets
                    } else {
                        val numberOfSubPackets = binaryInput.substring(cursor.index, cursor.index + 11).toInt(2)
                        cursor.index += 11
                        packet.subPackets = readPackets(binaryInput, maxPackets = numberOfSubPackets, cursor)
                    }
                }
            }
            result.add(packet)
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val binaryInput = input.first().map {
            it.digitToInt(16).toString(2).padStart(4, '0')
        }
            .joinToString(separator = "")
        return readPackets(binaryInput)
            .sumOf { it.versionSum() }
    }

    fun part2(input: List<String>): Long {
        val binaryInput = input.first().map {
            it.digitToInt(16).toString(2).padStart(4, '0')
        }
            .joinToString(separator = "")
        val readPackets = readPackets(binaryInput)
        return readPackets.single().resolve()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
//    check(part1(testInput) == 31)
    check(part2(testInput) == 1L)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}

private sealed class Packet(open val version: Int) {
    fun versionSum(): Int = when (this) {
        is Literal -> version
        is Operator -> version + subPackets.sumOf { it.versionSum() }
    }

    fun resolve(): Long = when (this) {
        is Literal -> number
        is Operator -> when (type) {
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
}

private data class Literal(override val version: Int, var number: Long = 0) : Packet(version)

private data class Operator(override val version: Int, val type: Int, var subPackets: List<Packet> = listOf()) :
    Packet(version)

data class Cursor(var index: Int)
