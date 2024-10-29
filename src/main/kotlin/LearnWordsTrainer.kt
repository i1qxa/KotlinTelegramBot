package org.example

import java.io.File

const val FILE_NAME = "words.txt"
const val MIN_CORRECT_ANSWER_COUNT = 3
const val AMOUNT_OF_WRONG_OPTIONS = 3

class LearnWordsTrainer(
    private val minCorrectAnswerCount: Int = MIN_CORRECT_ANSWER_COUNT,
    private val amountOfWrongOptions: Int = AMOUNT_OF_WRONG_OPTIONS,
) {


    val dictionary = mutableListOf<Word>()
    var currentQuestion: Question? = null

    init {
        loadDictionary()
        if (dictionary.isEmpty()) throw IllegalStateException("Некорректный файл")
    }

    private fun loadDictionary() {
        File(FILE_NAME).readLines().forEach { wordItem ->
            val splitLine = wordItem.split("|")
            val correctAnswerCount = try {
                splitLine[2].toIntOrNull() ?: 0
            } catch (e: Exception) {
                0
            }
            try {
                dictionary.add(Word(splitLine[0], splitLine[1], correctAnswerCount))
            } catch (_: Exception) {
            }
        }
    }

    fun saveDictionary() {
        File(FILE_NAME).writeText(dictionary.joinToString(separator = "\n") { it.toFileString() })
    }

    fun getStatistics(): Statistics {
        val wordsCount = dictionary.size
        val studyWordCount = dictionary.filter { it.correctAnswerCount >= minCorrectAnswerCount }.size
        val studyWordPercent = ((studyWordCount.toDouble() / wordsCount) * 100).toInt()
        return Statistics(
            wordsCount,
            studyWordCount,
            studyWordPercent
        )
    }

    fun getNextQuestion(): Boolean {
        val listOfNewWords = dictionary.filter { it.correctAnswerCount < minCorrectAnswerCount }
        if (listOfNewWords.isEmpty()) {
            return false
        }
        val listOfLearnedWords = dictionary.filter { it.correctAnswerCount >= minCorrectAnswerCount }
        val answer = listOfNewWords.shuffled()[0]
        val listOfOptions =
            listOfNewWords.shuffled().filter { it != answer }.take(amountOfWrongOptions).toMutableList()
        if (listOfOptions.size < amountOfWrongOptions) {
            val optionsRemain = amountOfWrongOptions - listOfOptions.size
            listOfOptions.addAll(listOfLearnedWords.shuffled().take(optionsRemain))
        }
        currentQuestion = Question(listOfOptions, answer)
        return true
    }

    fun checkAnswer(option: Int): Boolean {
        currentQuestion?.let { question ->
            if (question.checkAnswer(option)) {
                val newCorrectAnswerCount = question.answer.correctAnswerCount + 1
                dictionary.replaceAll { if (it == question.answer) question.answer.copy(correctAnswerCount = newCorrectAnswerCount) else it }
                saveDictionary()
                currentQuestion = null
                return true
            }else return false
        }
        return false
    }

}

data class Word(
    val original: String,
    val translated: String,
    val correctAnswerCount: Int,
) {
    fun toFileString() = "$original|$translated|$correctAnswerCount"
}