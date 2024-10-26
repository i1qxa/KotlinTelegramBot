package org.example.tg_servise

const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

enum class TgButtonsCallback(val btnDataString: String) {
    LEARN_WORDS(LEARN_WORDS_CLICKED),
    STATISTICS(STATISTICS_CLICKED),
    UNKNOWN(""),
    OPTION_ONE("${CALLBACK_DATA_ANSWER_PREFIX}1"),
    OPTION_TWO("${CALLBACK_DATA_ANSWER_PREFIX}2"),
    OPTION_THREE("${CALLBACK_DATA_ANSWER_PREFIX}3"),
    OPTION_FOUR("${CALLBACK_DATA_ANSWER_PREFIX}4"),
    ;

    companion object {
        fun getBtnFromString(btnData: String): TgButtonsCallback {
            return TgButtonsCallback.entries
                .find { it.btnDataString == btnData }
                ?: UNKNOWN
        }
    }
}