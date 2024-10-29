package org.example

import org.example.tg_servise.CALLBACK_DATA_ANSWER_PREFIX
import org.example.tg_servise.TelegramBotService
import org.example.tg_servise.TgButtonsCallback
import org.example.tg_servise.TgCommand

private val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
private val updateIdRegex = "\"update_id\":(.+?),".toRegex()
private val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()
private val dataRegex = "\"data\":\"(.+?)\"".toRegex()
private val optionsRegex = "\"data\":\"$CALLBACK_DATA_ANSWER_PREFIX([0-9])\"".toRegex()
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

        getOptionsData(updates)?.let {
            val option = it + 1
            if (trainer.checkAnswer(option)) {
                tgBotService.sendMessage(chatId, "Правильно")
            } else {
                tgBotService.sendMessage(
                    chatId,
                    "Неправильно! ${trainer.currentQuestion?.answer?.original} это ${trainer.currentQuestion?.answer?.translated}"
                )
            }
            checkNextQuestionAndSend(trainer, tgBotService, chatId)
        }

        getData(updates)?.let { btnClicked ->

            when (btnClicked) {
                TgButtonsCallback.LEARN_WORDS -> {
                    checkNextQuestionAndSend(trainer, tgBotService, chatId)
                }

                TgButtonsCallback.STATISTICS -> {
                    tgBotService.sendMessage(chatId, trainer.getStatistics().toString())
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

private fun getOptionsData(updates: String): Int? =
    optionsRegex.find(updates)
        ?.groups?.get(1)?.value?.toIntOrNull()

private fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    trainer.getNextQuestion()
    with(trainer.currentQuestion) {
        if (this == null) {
            telegramBotService.sendMessage(chatId, "Поздравляем, все слова выучены!")
        } else {
            telegramBotService.sendQuestion(chatId, this)
        }
    }

}