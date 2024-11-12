package org.example

import org.example.tg_servise.*

private const val TG_REFRESH_TIME_IN_MILS = 2000L
private const val HELLO_MSG = "Hello"

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0L
    val trainers = HashMap<Long, LearnWordsTrainer>()
    while (true) {
        Thread.sleep(TG_REFRESH_TIME_IN_MILS)
        val response = tgBotService.getUpdates(updateId)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdateId(it, tgBotService, trainers) }
        updateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdateId(update: Update, tgBotService: TelegramBotService, trainers: HashMap<Long, LearnWordsTrainer>) {
    println(update)
    val msg = getMsgText(update) ?: ""
    val chatId = getChatId(update) ?: return
    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer(fileName = "${chatId}.txt") }
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
    getOption(update)?.let { option ->
        trainer.currentQuestion?.let { question ->
            if (question.checkAnswer(option + 1)) {
                tgBotService.sendMessage(chatId, "Правильно!")
                trainer.dictionary.replaceAll {
                    if (it == question.answer)
                        question.answer.copy(correctAnswerCount = (question.answer.correctAnswerCount + 1))
                    else
                        it
                }
                trainer.saveDictionary()
            } else {
                tgBotService.sendMessage(
                    chatId,
                    "Неправильно! ${question.answer.original} это ${question.answer.translated}"
                )
            }
            checkNextQuestionAndSend(trainer, tgBotService, chatId)
        }
    }
    getData(update)?.let { btnClicked ->
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

            TgButtonsCallback.TG_MAIN_MENU -> {
                trainer.clearCurrentQuestion()
                tgBotService.sendMenu(chatId)
            }

            TgButtonsCallback.CLEAR_PROGRESS -> {
                trainer.clearProgress()
                tgBotService.sendMessage(chatId, "Прогресс сброшен")
            }
        }
    }
}

private fun getMsgText(update: Update): String? {
    return update.message?.text
}

private fun getChatId(update: Update): Long? {
    return if (update.message == null) update.callbackQuery?.message?.chat?.chatId
    else update.message.chat.chatId
}

private fun getData(update: Update): TgButtonsCallback? {
    val btnClickedAsString = update.callbackQuery?.data
    return if (btnClickedAsString != null) {
        TgButtonsCallback.getBtnFromString(btnClickedAsString)
    } else null
}


private fun getOption(update: Update): Int? {
    val answer = update.callbackQuery?.data
    return if (answer?.contains(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        answer.get((answer.length - 1)).toString().toIntOrNull()
    } else null
}


private fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long,
): Question? {
    val question = trainer.getNextQuestion()
    if (question == null) {
        telegramBotService.sendMessage(chatId, "Поздравляем, все слова выучены!")
    } else {
        telegramBotService.sendQuestion(chatId, question)
    }
    return question
}

