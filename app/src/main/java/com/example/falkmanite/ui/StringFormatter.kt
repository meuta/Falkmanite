package com.example.falkmanite.ui

class StringFormatter {
    private val pattern: String = "%02d:%02d"
    fun format(input: Int): String = pattern.format(
        input / 60_000,
        input % 60_000 / 1000
    )
}