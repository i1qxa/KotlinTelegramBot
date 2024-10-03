package org.example

import java.io.File

const val FILE_NAME = "words.txt"

fun main() {

    val dictionary = mutableListOf<Word>()
    File(FILE_NAME).readLines().forEach { wordItem ->
        val splitLine = wordItem.split("|")
        try {
            dictionary.add(Word(splitLine[0], splitLine[1], splitLine[2].toIntOrNull() ?: 0))
        }catch (e:Exception){
            throw RuntimeException("Слово или оригинал пустые.")
        }
    }
    println(dictionary.joinToString(separator = "\n"))

}

data class Word(
    val original: String,
    val translated: String,
    val correctAnswerCount: Int,
)