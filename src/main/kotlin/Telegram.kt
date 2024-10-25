package org.example

import org.example.tg_servise.TelegramBotService
import org.example.tg_servise.TgButtonsCallback
import org.example.tg_servise.TgCommand

private val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
private val updateIdRegex = "\"update_id\":(.+?),".toRegex()
private val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()
private val dataRegex = "\"data\":\"(.+?)\"".toRegex()
private const val TG_REFRESH_TIME_IN_MILS = 2000L
private const val HELLO_MSG = "Hello"

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0
    val trainer = LearnWordsTrainer()
    while (true) {
        Thread.sleep(TG_REFRESH_TIME_IN_MILS)
        val updates = tgBotService.getUpdates(updateId)
        getUpdateId(updates)?.let {
            updateId = it + 1
        }
        val msg = getMsgText(updates) ?: ""
        println(msg)
        val chatId = getChatId(updates) ?: continue
        val command = TgCommand.getTgCommandFromString(msg)
        when (command) {
            TgCommand.HELLO -> {
                tgBotService.sendMessage(chatId, HELLO_MSG)
            }

            TgCommand.START -> {
                tgBotService.sendMenu(chatId)
            }

            TgCommand.UNKNOWN -> {
                Unit
            }
        }
        getData(updates)?.let { btnClicked ->
            when (btnClicked) {
                TgButtonsCallback.LEARN_WORDS -> {
                    TODO("Need to implement Learning Words")
                }

                TgButtonsCallback.STATISTICS -> {
                    tgBotService.sendMessage(chatId, "Выучено 10 из 10 слов 100%")
                }

                TgButtonsCallback.UNKNOWN -> {
                    Unit
                }
            }
        }
    }
}

private fun getMsgText(updates: String): String? {
    val matchResult = messageTextRegex.find(updates)
    return matchResult?.groups?.get(1)?.value
}

private fun getUpdateId(updates: String): Int? {
    val matchResult = updateIdRegex.find(updates)
    return matchResult?.groups?.get(1)?.value?.toIntOrNull()
}

private fun getChatId(updates: String): Long? {
    val matchResult = chatIdRegex.find(updates)
    return matchResult?.groups?.get(1)?.value?.toLongOrNull()
}

private fun getData(updates: String): TgButtonsCallback? =
    dataRegex.find(updates)
        ?.groups?.get(1)?.value
        ?.let { TgButtonsCallback.getBtnFromString(it) }

