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
    var question: Question? = null
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
                    question = trainer.getNextQuestion()
                    if (question == null){
                        tgBotService.sendMessage(chatId, "Поздравляем, все слова выучены!")
                    }else{
                        tgBotService.sendQuestion(chatId, question)
                    }
                }

                TgButtonsCallback.STATISTICS -> {
                    tgBotService.sendMessage(chatId, trainer.getStatistics().toString())
                }

                TgButtonsCallback.UNKNOWN -> {
                    Unit
                }

                TgButtonsCallback.OPTION_ONE -> TODO()
                TgButtonsCallback.OPTION_TWO -> TODO()
                TgButtonsCallback.OPTION_THREE -> TODO()
                TgButtonsCallback.OPTION_FOUR -> TODO()
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

