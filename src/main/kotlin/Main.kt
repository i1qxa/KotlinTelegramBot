package org.example

import java.io.File

const val FILE_NAME = "words.txt"

fun main() {

    val dictionary = mutableListOf<Word>()
    File(FILE_NAME).readLines().forEach { wordItem ->
        val splitLine = wordItem.split("|")
        val correctAnswerCount = try {
            splitLine[2].toInt()
        } catch (e: Exception) {
            0
        }
        try {
            dictionary.add(Word(splitLine[0], splitLine[1], correctAnswerCount))
        } catch (e: Exception) {
            throw RuntimeException("Слово или оригинал пустые.")
        }
    }
    printMenuInfo()
    while (true) {
        when (readln()) {
            "1" -> println("Нажата кнопка\"1\"")
            "2" -> printStatistic(dictionary)
            "0" -> break
            else -> println("Неизвестная команда")
        }
    }
//    println(dictionary.joinToString(separator = "\n"))

}

fun printMenuInfo() {
    println(
        "Меню:\n" +
                "1 - Учить слова\n" +
                "2 - Статистика\n" +
                "0 - Выход"
    )
}

fun printStatistic(dictionary: List<Word>) {
    val wordsCount = dictionary.size
    val studyWordCount = dictionary.filter { it.correctAnswerCount >= 3 }.size
    val studyWordPercent = ((studyWordCount.toDouble() / wordsCount) * 100).toInt()
    println("Выучено $studyWordCount из $wordsCount | $studyWordPercent%")
}

data class Word(
    val original: String,
    val translated: String,
    val correctAnswerCount: Int,
)