package org.example

import java.io.File

class LearnWordsTrainer {

    val dictionary = mutableListOf<Word>()

    fun loadDictionary(){
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
    }

    init {
        loadDictionary()
    }

    fun saveDictionary(dictionary: List<Word>) {
        File(FILE_NAME).writeText(dictionary.joinToString(separator = "\n") { it.toFileString() })
    }

    fun getStatistics():Statistics{
        val wordsCount = dictionary.size
        val studyWordCount = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWER_COUNT }.size
        val studyWordPercent = ((studyWordCount.toDouble() / wordsCount) * 100).toInt()
        return Statistics(
            wordsCount,
            studyWordCount,
            studyWordPercent
        )
    }

    fun getNextQuestion():Question?{
        val listOfNewWords = dictionary.filter { it.correctAnswerCount < MIN_CORRECT_ANSWER_COUNT }
        if (listOfNewWords.isEmpty()) {
            println("Все слова выучены")
            return null
        }
        val listOfLearnedWords = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWER_COUNT }

    }

}