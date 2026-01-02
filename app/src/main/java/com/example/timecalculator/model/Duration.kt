package com.example.timecalculator.model

data class Duration(
    val days: Int,
    val hours: Int,
    val minutes: Int
) {

    companion object {
        val ZERO = Duration(0, 0, 0)
    }

    operator fun plus(other: Duration): Duration {
        val totalMinutes = this.toMinutes() + other.toMinutes()
        return fromMinutes(totalMinutes)
    }

    private fun toMinutes(): Int =
        days * 24 * 60 + hours * 60 + minutes

    private fun fromMinutes(total: Int): Duration {
        val d = total / (24 * 60)
        val h = (total % (24 * 60)) / 60
        val m = total % 60
        return Duration(d, h, m)
    }


    override fun toString(): String =
        "${days}d ${hours}h ${minutes}m"
}
