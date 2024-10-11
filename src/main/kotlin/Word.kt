package org.example

data class Word(
    val original: String,
    val translated: String,
    val correctAnswerCount: Int,
) {
    fun toFileString() = "$original|$translated|$correctAnswerCount"
}