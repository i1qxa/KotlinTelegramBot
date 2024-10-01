package org.example

import java.io.File

const val FILE_NAME = "words.txt"

fun main() {

    val dictionary = mutableListOf<Word>()
    File(FILE_NAME).readLines().forEach { wordItem ->
        wordItem.split("|").apply {
            if (validateDictionaryString(this)) {
                val numberLearnedWord = try {
                    this[2].toInt()
                } catch (e: Exception) {
                    0
                }
                dictionary.add(Word(this[0], this[1], numberLearnedWord))
            }
        }
    }
    println(dictionary.joinToString(separator = "\n"))

}

fun validateDictionaryString(listWord: List<String>): Boolean {
    return try {
        listWord[0].isNotEmpty() && listWord[1].isNotEmpty()
    } catch (e: Exception) {
        false
    }
}

data class Word(
    val original: String,
    val translated: String,
    val numberLearnedWord: Int,
)