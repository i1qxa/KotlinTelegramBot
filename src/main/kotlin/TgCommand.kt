package org.example

enum class TgCommand() {
    HELLO,
    START,
    UNKNOWN;

    companion object {
        fun getTgCommandFromString(commandStr: String?): TgCommand? {
            return when (commandStr) {
                "Hello" -> HELLO
                "/start" -> START
                "" -> null
                null -> null
                else -> UNKNOWN
            }
        }
    }

}