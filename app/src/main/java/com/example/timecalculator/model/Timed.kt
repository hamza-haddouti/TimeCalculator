package com.example.timecalculator.model

data class Timed<T>(
    val duration: Duration,
    val value: T
)
