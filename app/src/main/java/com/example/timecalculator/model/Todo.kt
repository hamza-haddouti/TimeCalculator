package com.example.timecalculator.model

data class Todo(
    val description: String,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var finished: Boolean = false
) {
    fun durationMillis(): Long = (endTime ?: System.currentTimeMillis()) - startTime
}
