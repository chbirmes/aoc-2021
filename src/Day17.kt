fun main() {

    fun shotHitsTarget(initialVelocity: Pair<Int, Int>, xTarget: IntRange, yTarget: IntRange): Boolean {
        var position = 0 to 0
        var velocity = initialVelocity
        while (position.first <= xTarget.last && position.second >= yTarget.first) {
            position = (position.first + velocity.first) to (position.second + velocity.second)
            velocity = (if (velocity.first > 0) velocity.first - 1 else 0) to (velocity.second - 1)
            if (position.first in xTarget && position.second in yTarget) {
                return true
            }
        }
        return false
    }

    fun part1(yTarget: IntRange): Int {
        fun Int.gaussSum() = (this * this + this) / 2
        return (-yTarget.first - 1).gaussSum()
    }

    fun part2(xTarget: IntRange, yTarget: IntRange): Int {
        return 1.rangeTo(xTarget.last).flatMap { x ->
            yTarget.first.rangeTo(-yTarget.first).map { y -> x to y }
        }.count { shotHitsTarget(it, xTarget, yTarget) }
    }

    // test if implementation meets criteria from the description, like:
    check(part1(-10..-5) == 45)
    check(part2(20..30, -10..-5) == 112)

    println(part1(-189..-148))
    println(part2(48..70, -189..-148))
}

