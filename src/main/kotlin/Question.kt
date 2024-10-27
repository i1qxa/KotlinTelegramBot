package org.example

data class Question(
    val listOptions: List<Word>,
    val answer: Word,
) {

    val questionAsList by lazy {
        val listQuestion = listOptions.toMutableList()
        listQuestion.add(answer)
        listQuestion.shuffle()
        listQuestion
    }

    fun checkAnswer(variantNum: Int): Boolean {
        val index = variantNum - 1
        return try {
            questionAsList[index] == answer
        } catch (e: Exception) {
            println("Вариант ответа должен быть в пределах 1..4")
            false
        }
    }

    fun printQuestion() {
        println("Как переводится слово: ${answer.original}")
        var counter = 1
        questionAsList.forEach {
            println("${counter++} - ${it.translated}")
        }
        println("----------\n0 - Меню")
    }

}
