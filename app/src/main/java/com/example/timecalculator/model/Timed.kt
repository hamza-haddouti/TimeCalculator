package com.example.timecalculator.model

import com.example.timecalculator.model.Duration

data class Timed<T>(
    val duration: Duration,
    val value: T
) {

    fun <U> map(f: (T) -> U): Timed<U> =
        Timed(duration, f(value))

    fun <U> flatMap(f: (T) -> Timed<U>): Timed<U> {
        val next = f(value)
        return Timed(
            duration = duration + next.duration,
            value = next.value
        )
    }

    companion object {

        fun <T> pure(value: T): Timed<T> =
            Timed(Duration.ZERO, value)

        fun <T> combine(items: List<Timed<T>>): Timed<List<T>> =
            items.fold(pure(emptyList())) { acc, timed ->
                acc.flatMap { list ->
                    timed.map { value -> list + value }
                }
            }
    }

}
