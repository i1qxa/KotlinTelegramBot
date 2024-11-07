package org.example

const val MIN_CORRECT_ANSWER_COUNT = 3
const val AMOUNT_OF_WRONG_OPTIONS = 3

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Не корректный файл")
        return
    }
    while (true) {
        printMenuInfo()
        when (readln()) {
            "1" -> learnWords(trainer)
            "2" -> println(trainer.getStatistics())
            "0" -> break
            else -> println("Неизвестная команда")
        }
    }

}

fun learnWords(trainer: LearnWordsTrainer) {
    do {
        val question = trainer.getNextQuestion()
        if (question == null) {
            println("Все слова выучены")
            printMenuInfo()
            return
        } else {
            question.printQuestion()
            when (val userAnswerInput = readln().toIntOrNull()) {
                in 1..4 -> {
                    userAnswerInput?.let {
                        if (question.checkAnswer(it)) {
                            trainer.dictionary.replaceAll { userAnswerInput ->
                                if (userAnswerInput == question.answer) question.answer.copy(
                                    correctAnswerCount = question.answer.correctAnswerCount + 1
                                ) else userAnswerInput
                            }
                            trainer.saveDictionary()
                            println("Правильно!")
                        } else println("Неправильно! ${question.answer.original} - это ${question.answer.translated}")
                    }

                }

                0 -> {
                    printMenuInfo()
                    return
                }

                else -> println("Не правильно выбран вариант ответа. Необходимо выбрать одно из четырех слов(1,2,3,4) или 0 для возврата в главное меню")
            }
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

