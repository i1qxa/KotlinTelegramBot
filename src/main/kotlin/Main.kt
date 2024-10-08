package org.example

import java.io.File

const val FILE_NAME = "words.txt"
const val MIN_CORRECT_ANSWER_COUNT = 3
const val AMOUNT_OF_WRONG_OPTIONS = 3

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
    val listOfLearnedWords = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWER_COUNT }
    if (listOfNewWords.isEmpty()) {
        println("Все слова выучены")
        return
    }
    do {
        val wordForTranslate = listOfNewWords.shuffled().first()
        val listOfOptions =
            listOfNewWords.shuffled().filter { it != wordForTranslate }.take(3).toMutableList()
        if (listOfOptions.size < AMOUNT_OF_WRONG_OPTIONS) {
            val optionsRemaining = AMOUNT_OF_WRONG_OPTIONS - listOfOptions.size
            listOfOptions.addAll(listOfLearnedWords.shuffled().take(optionsRemaining))
        }
        listOfOptions.add(wordForTranslate)
        listOfOptions.shuffle()
        println("Как переводится слово: ${wordForTranslate.original}")
        var counter = 1
        listOfOptions.forEach {
            println("${counter++} - ${it.translated}")
        }
        when (val answer = readln().toIntOrNull()) {
            in 1..4 -> {
                println("Вы выбрали: ${listOfOptions[answer!! - 1]}")
            }

            0 -> {
                printMenuInfo()
                return
            }

            else -> println("Не правильно выбран вариант ответа. Необходимо выбрать одно из четырех слов(1,2,3,4) или 0 для возврата в главное меню")
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