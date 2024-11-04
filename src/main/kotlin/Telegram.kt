package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.tg_servise.CALLBACK_DATA_ANSWER_PREFIX
import org.example.tg_servise.TelegramBotService
import org.example.tg_servise.TgButtonsCallback
import org.example.tg_servise.TgCommand

private const val TG_REFRESH_TIME_IN_MILS = 2000L
private const val HELLO_MSG = "Hello"

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0L
    val trainer = LearnWordsTrainer()
    var currentQuestion: Question? = null
    var update: Update? = null
    while (true) {
        Thread.sleep(TG_REFRESH_TIME_IN_MILS)
        update = tgBotService.getUpdates(updateId).result.firstOrNull() ?: continue
        println(update)
        println("\n-----------------------------\n")
        println(update)
        updateId = update.updateId + 1
        val msg = getMsgText(update) ?: ""
        val chatId = getChatId(update) ?: continue
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
            currentQuestion?.let { question ->
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
                currentQuestion = checkNextQuestionAndSend(trainer, tgBotService, chatId)
            }
        }
        getData(update)?.let { btnClicked ->
            when (btnClicked) {
                TgButtonsCallback.LEARN_WORDS -> {
                    currentQuestion = checkNextQuestionAndSend(trainer, tgBotService, chatId)
                }

                TgButtonsCallback.STATISTICS -> {
                    tgBotService.sendMessage(chatId, trainer.getStatistics().toString())
                }

                TgButtonsCallback.UNKNOWN -> {
                    Unit
                }

                TgButtonsCallback.TG_MAIN_MENU -> {
                    currentQuestion = null
                    tgBotService.sendMenu(chatId)
                }
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

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("message_id")
    val messageId: Long,
    @SerialName("chat")
    val chat: Chat,
    @SerialName("text")
    val text: String,
)

@Serializable
data class Chat(
    @SerialName("id")
    val chatId: Long,
)

@Serializable
data class CallbackQuery(
    @SerialName("message")
    val message: Message? = null,
    @SerialName("data")
    val data: String,
)
