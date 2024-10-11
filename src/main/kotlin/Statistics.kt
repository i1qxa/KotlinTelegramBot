package org.example

data class Statistics(
    val wordsCount: Int,
    val studyWordCount: Int,
    val studyWordPercent: Int,
) {
    override fun toString(): String {
        return "Выучено $studyWordCount из $wordsCount | $studyWordPercent%"
    }
}
