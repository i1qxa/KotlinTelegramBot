package org.example

import java.io.File

const val BASE_DICTIONARY_FILE_NAME = "words.txt"

class LearnWordsTrainer(
    private val minCorrectAnswerCount: Int = MIN_CORRECT_ANSWER_COUNT,
    private val amountOfWrongOptions: Int = AMOUNT_OF_WRONG_OPTIONS,
    private val fileName: String = "words.txt",
) {
    val dictionary = mutableListOf<Word>()
    var currentQuestion: Question? = null
    private val currentUserFile: File
        get() {
            return if (File(fileName).exists()) {
                File(fileName)
            } else {
                File(BASE_DICTIONARY_FILE_NAME).copyTo(File(fileName), true)
            }
        }

    init {
        loadDictionary()
        if (dictionary.isEmpty()) throw IllegalStateException("Некорректный файл")
    }

    private fun loadDictionary() {

        currentUserFile.readLines().forEach { wordItem ->
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
        currentUserFile.writeText(dictionary.joinToString(separator = "\n") { it.toFileString() })
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

    fun getNextQuestion(): Question? {
        val listOfNewWords = dictionary.filter { it.correctAnswerCount < minCorrectAnswerCount }
        if (listOfNewWords.isEmpty()) {
            return null
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
        return currentQuestion
    }

    fun clearCurrentQuestion() {
        currentQuestion = null
    }

    fun clearProgress() {
        dictionary.replaceAll { word ->
            word.copy(correctAnswerCount = 0)
        }
        saveDictionary()
    }

}

data class Word(
    val original: String,
    val translated: String,
    val correctAnswerCount: Int,
) {
    fun toFileString() = "$original|$translated|$correctAnswerCount"
}