package org.example

data class Question(
    val listVariants: List<Word>,
    val answer: Word,
) {

//    private lateinit var questionAsList: List<Word>

    val questionAsList by lazy {
        val listQuestion = listVariants.toMutableList()
        listQuestion.add(answer)
        listQuestion.shuffle()
        listQuestion
    }

//    fun getQuestionAsList(): List<Word> {
//        val listQuestion = listVariants.toMutableList()
//        listQuestion.add(answer)
//        listQuestion.shuffle()
//        questionAsList = listQuestion
//        return questionAsList
//    }

    fun checkAnswer(variantNum: Int):Boolean {
        val index = variantNum - 1
        return questionAsList[index] == answer
    }

}
