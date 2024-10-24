package org.example.tg_servise

enum class TgCommand(val command: String) {
    HELLO("Hello"),
    START("/start"),
    UNKNOWN("");

    companion object {
        fun getTgCommandFromString(commandStr: String): TgCommand {
            return TgCommand.entries.find {
                it.command == commandStr
            } ?: UNKNOWN
        }
    }

}