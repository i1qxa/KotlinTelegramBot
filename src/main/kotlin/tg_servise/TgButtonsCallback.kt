package org.example.tg_servise

enum class TgButtonsCallback(val btnDataString: String) {
    LEARN_WORDS(LEARN_WORDS_CLICKED),
    STATISTICS(STATISTICS_CLICKED),
    UNKNOWN("");

    companion object {
        fun getBtnFromString(btnData: String): TgButtonsCallback {
            return TgButtonsCallback.entries
                .find { it.btnDataString == btnData }
                ?: UNKNOWN
        }
    }
}