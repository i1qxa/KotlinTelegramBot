package org.example

import java.io.File

const val FILE_NAME = "words.txt"
const val MIN_CORRECT_ANSWER_COUNT = 3

fun main() {

    val dictionary = mutableListOf<Word>()
    File(FILE_NAME).readLines().forEach { wordItem ->
        val splitLine = wordItem.split("|")
        val correctAnswerCount = try {
            splitLine[2].toInt()
        } catch (e: Exception) {
            println("Нет информации о количестве повторений этого слова")
            0
        }
        try {
            dictionary.add(Word(splitLine[0], splitLine[1], correctAnswerCount))
        } catch (e: Exception) {
            println("Слово или оригинал пустые.")
        }
    }
    printMenuInfo()
    while (true) {
        when (readln()) {
            "1" -> learnWords(dictionary)
            "2" -> printStatistic(dictionary)
            "0" -> break
            else -> println("Неизвестная команда")
        }
    }

}

fun learnWords(dictionary: List<Word>) {
    println("Для выхода из режима изучения введите \"0\"")
    val listOfNewWords = dictionary.filter { it.correctAnswerCount < MIN_CORRECT_ANSWER_COUNT }
    if (listOfNewWords.isEmpty()) {
        println("Все слова выучены")
        return
    }
    do {
        val wordForTranslate = listOfNewWords.shuffled().first()
        val listOfVariant =
            dictionary.shuffled().filter { it != wordForTranslate }.sortedBy { it.correctAnswerCount }.take(3)
                .toMutableList()
        listOfVariant.add(wordForTranslate)
        listOfVariant.shuffle()
        println("Как переводится слово: ${wordForTranslate.original}")
        var counter = 1
        listOfVariant.forEach {
            println("${counter++} - ${it.translated}")
        }
        when (val answer = readln().toIntOrNull()) {
            in 1..4 -> {
                println("Вы выбрали: ${listOfVariant[answer!! - 1]}")
            }

            0 -> {
                printMenuInfo()
                return
            }

            else -> println("Не правильно выбран вариант ответа")
        }
    } while (true)
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
    val studyWordCount = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWER_COUNT }.size
    val studyWordPercent = ((studyWordCount.toDouble() / wordsCount) * 100).toInt()
    println("Выучено $studyWordCount из $wordsCount | $studyWordPercent%")
}

data class Word(
    val original: String,
    val translated: String,
    val correctAnswerCount: Int,
)