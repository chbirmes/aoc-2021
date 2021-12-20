fun main() {

    fun part1(input: List<String>): Int {
        return input.map { SnailfishNumber.parse(it) }
            .reduce(SnailfishNumber::plus)
            .magnitude()
    }

    fun part2(input: List<String>): Int {
        val snailfishNumbers = input.map { SnailfishNumber.parse(it) }
        return snailfishNumbers.flatMap { a ->
            snailfishNumbers.map { b -> a to b }
        }
            .filter { (a, b) -> a != b }
            .map { (a, b) -> a + b }
            .map { it.magnitude() }
            .maxOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

private sealed class SnailfishNumber {

    var parent: Pair? = null

    class Pair(var left: SnailfishNumber, var right: SnailfishNumber) : SnailfishNumber() {

        init {
            left.parent = this
            right.parent = this
        }

        override fun toString() = "[$left,$right]"

        fun explode() {
            val nextLeftNumber = findAncestor { parent, child -> parent.right == child }?.left?.rightMostNumber()
            nextLeftNumber?.let { it.value += (left as Number).value }
            val nextRightNumber = findAncestor { parent, child -> parent.left == child }?.right?.leftMostNumber()
            nextRightNumber?.let { it.value += (right as Number).value }
            replaceBy(Number(0))
        }
    }

    class Number(var value: Int) : SnailfishNumber() {

        override fun toString() = "$value"

        fun split() {
            replaceBy(Pair(Number(value / 2), Number(value / 2 + value % 2)))
        }
    }

    private fun copy(): SnailfishNumber = when (this) {
        is Number -> Number(value)
        is Pair -> Pair(left.copy(), right.copy())
    }

    protected fun replaceBy(other: SnailfishNumber) {
        other.parent = parent
        parent?.let {
            if (it.left == this) {
                it.left = other
            } else {
                it.right = other
            }
        }
    }

    operator fun plus(other: SnailfishNumber) = Pair(this.copy(), other.copy()).apply { reduce() }

    protected fun reduce() {
        var explodable = nextExplodable()
        var splittable = nextSplittable()
        while (explodable != null || splittable != null) {
            explodable?.explode() ?: splittable!!.split()
            explodable = nextExplodable()
            splittable = nextSplittable()
        }
    }

    private fun nextExplodable() = leftMostPairAtDepth { it >= 4 }

    private fun nextSplittable() = leftMostNumber { it >= 10 }

    protected fun findAncestor(predicate: (parent: Pair, child: SnailfishNumber) -> Boolean): Pair? {
        return parent?.let {
            if (predicate(it, this)) {
                it
            } else {
                it.findAncestor(predicate)
            }
        }
    }

    protected fun leftMostPairAtDepth(predicate: (depth: Int) -> Boolean): Pair? = when (this) {
        is Number -> null
        is Pair -> {
            if (predicate(depth()))
                this
            else
                left.leftMostPairAtDepth(predicate) ?: right.leftMostPairAtDepth(predicate)
        }
    }

    protected fun depth(): Int = parent?.let { it.depth() + 1 } ?: 0

    protected fun leftMostNumber(predicate: (value: Int) -> Boolean = { true }): Number? = when (this) {
        is Number -> if (predicate(value)) this else null
        is Pair -> left.leftMostNumber(predicate) ?: right.leftMostNumber(predicate)
    }

    protected fun rightMostNumber(): Number? = when (this) {
        is Number -> this
        is Pair -> right.rightMostNumber() ?: left.rightMostNumber()
    }

    fun magnitude(): Int =
        when (this) {
            is Number -> value
            is Pair -> 3 * left.magnitude() + 2 * right.magnitude()
        }

    companion object {
        fun parse(s: String): SnailfishNumber {
            return if (s.first().isDigit()) {
                Number(s.first().digitToInt())
            } else {
                val index = s.indexOfPairSeparatingComma()
                val left = parse(s.substring(1, index))
                val right = parse(s.substring(index + 1, s.length - 1))
                Pair(left, right)
            }
        }
    }
}

private fun String.indexOfPairSeparatingComma(): Int {
    var openCounter = 0
    forEachIndexed { index, c ->
        if (openCounter == 1 && c == ',') {
            return index
        } else if (c == '[') {
            openCounter++
        } else if (c == ']') {
            openCounter--
        }
    }
    throw IllegalStateException()
}
