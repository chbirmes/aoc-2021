fun main() {

    fun readAmphipods(input: List<String>): Set<Amphipod> {
        return input.drop(2).dropLast(1).flatMapIndexed { index, s ->
            s.filter { it in "ABCD" }.let {
                listOf(
                    Amphipod(Color.of(it[0]), Room(Color.Amber, index)),
                    Amphipod(Color.of(it[1]), Room(Color.Bronze, index)),
                    Amphipod(Color.of(it[2]), Room(Color.Copper, index)),
                    Amphipod(Color.of(it[3]), Room(Color.Desert, index))
                )
            }
        }.toSet()
    }


    fun part1(input: List<String>): Int {
        State.resetCaches()
        Room.maxDepth = 1
        val startState = State(readAmphipods(input), 0)
        return startState.costToFinish() ?: throw IllegalStateException()
    }

    fun part2(input: List<String>): Int {
        State.resetCaches()
        Room.maxDepth = 3
        val adjustedInput = input.take(3) + listOf("DCBA", "DBAC") + input.drop(3)
        val startState = State(readAmphipods(adjustedInput), 0)
        return startState.costToFinish() ?: throw IllegalStateException()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 12521)
    check(part2(testInput) == 44169)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}


private class State(val amphipods: Set<Amphipod>, val accumulatedCost: Int) {

    fun costToFinish(): Int? {
        val cachedCostToHere = cache[amphipods]
        if (cachedCostToHere != null && accumulatedCost >= cachedCostToHere) {
            return null
        } else {
            cache[amphipods] = accumulatedCost
        }
        if (accumulatedCost >= minCost || (deadlock())) {
            return null
        }
        return if (amphipods.all { it.isAtDestination() }) accumulatedCost.also { minCost = it }
        else {
            val amphipodMoves = possibleMoves()
            if (amphipodMoves.isEmpty()) {
                null
            } else {
                amphipodMoves.mapNotNull { (amphipod, move) ->
                    executeMove(amphipod, move).costToFinish()
                }.minOrNull()
            }
        }
    }

    private fun deadlock(): Boolean {
        val inCentralHallway = amphipods.filter { it.position is Hallway && it.position.square in 2..8 }
        val destinationToTheRight = inCentralHallway.filter { (it.position as Hallway) < it.color.roomEntrance }
        val destinationToTheLeft = inCentralHallway.filter { (it.position as Hallway) > it.color.roomEntrance }
        return destinationToTheRight.any { left ->
            destinationToTheLeft.any { right ->
                val leftPosition = (left.position as Hallway)
                val rightPosition = (right.position as Hallway)
                left.color.roomEntrance >= rightPosition && right.color.roomEntrance <= leftPosition
            }
        }
    }

    fun possibleMoves(): List<Pair<Amphipod, Move>> {
        val pairs = amphipods.flatMap { amphipod ->
            amphipod.legalMoves(amphipods).map { amphipod to it }
        }
        val (room, hallway) = pairs.partition { it.second.target is Room }
        return room.ifEmpty { hallway }
    }

    fun executeMove(amphipod: Amphipod, move: Move): State {
        val newAmphipods = amphipods - amphipod + Amphipod(amphipod.color, move.target)
        val cost = move.route().size * amphipod.color.movementCost
        return State(newAmphipods, accumulatedCost + cost)
    }

    companion object {
        var minCost = Int.MAX_VALUE
        val cache = mutableMapOf<Set<Amphipod>, Int>()

        fun resetCaches() {
            minCost = Int.MAX_VALUE
            cache.clear()
        }
    }

}

private data class Amphipod(val color: Color, val position: Position) {

    fun legalMoves(amphipods: Set<Amphipod>): List<Move> {
        val legalTargets: Set<Position> = when (position) {
            is Hallway -> Room.legalMoveTargets(color, amphipods)
            is Room -> if (position.color == color && position.squaresBelowCorrectlyOccupied(amphipods)) emptySet()
            else {
                val roomTargets = (if (position.color != color) Room.legalMoveTargets(color, amphipods)
                else emptySet())
                Hallway.legalMoveTargets + roomTargets
            }

        }
        val occupiedPositions = amphipods.map { it.position }.toSet()
        return (legalTargets - occupiedPositions).map { Move(position, it) }
            .filter { move -> move.route().none { it in occupiedPositions } }
    }

    fun isAtDestination() = (position is Room) && position.color == color

}

private enum class Color(val movementCost: Int, val roomEntrance: Hallway) {
    Amber(1, Hallway(2)), Bronze(10, Hallway(4)), Copper(100, Hallway(6)), Desert(1000, Hallway(8));

    companion object {

        fun of(c: Char) = when (c) {
            'A' -> Amber
            'B' -> Bronze
            'C' -> Copper
            'D' -> Desert
            else -> throw IllegalArgumentException()
        }
    }
}

private sealed class Position

private data class Hallway(val square: Int) : Position(), Comparable<Hallway> {

    fun routeTo(other: Hallway): Set<Position> {
        val squares = if (square < other.square) (square + 1)..other.square
        else other.square until square
        return squares.map { Hallway(it) }.toSet()
    }

    companion object {
        val legalMoveTargets = setOf(0, 1, 3, 5, 7, 9, 10).map { Hallway(it) }.toSet()
    }

    override fun compareTo(other: Hallway) = square.compareTo(other.square)

}

private data class Room(val color: Color, val depth: Int) : Position() {

    fun squaresBelowCorrectlyOccupied(amphipods: Set<Amphipod>) =
        ((depth + 1)..maxDepth).all { Amphipod(color, Room(color, it)) in amphipods }

    fun routeOut() = (0 until depth).map { Room(color, it) }.toSet() + entrance()

    fun routeIn() = (0..depth).map { Room(color, it) }.toSet()

    fun entrance() = color.roomEntrance

    companion object {
        var maxDepth = 1

        fun legalMoveTargets(color: Color, amphipods: Set<Amphipod>) =
            (0..maxDepth).map { Room(color, it) }.filter { it.squaresBelowCorrectlyOccupied(amphipods) }.toSet()
    }
}

private data class Move(val start: Position, val target: Position) {

    fun route(): Set<Position> =
        when (start) {
            is Hallway -> when (target) {
                is Hallway -> throw IllegalStateException()
                is Room -> start.routeTo(target.entrance()) + target.routeIn()
            }
            is Room -> when (target) {
                is Hallway -> start.routeOut() + start.entrance().routeTo(target)
                is Room -> {
                    if (start.color == target.color) {
                        throw IllegalStateException()
                    }
                    start.routeOut() + start.entrance().routeTo(target.entrance()) + target.routeIn()
                }
            }
        }
}
