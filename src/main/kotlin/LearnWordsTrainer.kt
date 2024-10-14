package org.example

import java.io.File

class LearnWordsTrainer {


    val dictionary = mutableListOf<Word>()

    init {
        loadDictionary()
        if (dictionary.isEmpty()) throw IllegalStateException("Некорректный файл")
    }

    private fun loadDictionary() {
        File(FILE_NAME).readLines().forEach { wordItem ->
            val splitLine = wordItem.split("|")
            val correctAnswerCount = try {
                splitLine[2].toIntOrNull() ?: 0
            } catch (e:Exception){
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
        val studyWordCount = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWER_COUNT }.size
        val studyWordPercent = ((studyWordCount.toDouble() / wordsCount) * 100).toInt()
        return Statistics(
            wordsCount,
            studyWordCount,
            studyWordPercent
        )
    }

    fun getNextQuestion(): Question? {
        val listOfNewWords = dictionary.filter { it.correctAnswerCount < MIN_CORRECT_ANSWER_COUNT }
        if (listOfNewWords.isEmpty()) {
            return null
        }
        val listOfLearnedWords = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWER_COUNT }
        val answer = listOfNewWords.shuffled()[0]
        val listOfOptions =
            listOfNewWords.shuffled().filter { it != answer }.take(AMOUNT_OF_WRONG_OPTIONS).toMutableList()
        if (listOfOptions.size < AMOUNT_OF_WRONG_OPTIONS) {
            val optionsRemain = AMOUNT_OF_WRONG_OPTIONS - listOfOptions.size
            listOfOptions.addAll(listOfLearnedWords.shuffled().take(optionsRemain))
        }
        return Question(listOfOptions, answer)
    }

}