package org.example.tg_servise

enum class TgButtons(val btnDataString: String) {
    LEARN_WORDS(LEARN_WORDS_CLICKED),
    STATISTICS(STATISTICS_CLICKED),
    UNKNOWN("");

    companion object {
        fun getBtnFromString(btnData: String?): TgButtons? {
            return when (btnData) {
                LEARN_WORDS_CLICKED -> LEARN_WORDS
                STATISTICS_CLICKED -> STATISTICS
                "" -> null
                null -> null
                else -> UNKNOWN
            }
        }
    }
}