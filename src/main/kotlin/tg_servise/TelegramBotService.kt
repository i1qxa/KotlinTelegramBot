package org.example.tg_servise

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.Question
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

private const val BASE_URL = "https://api.telegram.org/bot"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService(private val token: String) {

    private val client = HttpClient.newBuilder().build()
    private val json = Json { ignoreUnknownKeys = true }

    fun sendMessage(chatId: Long, text: String) {
        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )
        val urlSendMessage = "$BASE_URL$token/sendMessage?chat_id=$chatId&text=$encoded"
        val requestSendMessage = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString()).body()
    }

    fun getUpdates(updateId: Long): Response {
        val urlGetUpdates = "$BASE_URL$token/getUpdates?offset=$updateId"
        val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val update = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).body()
        return json.decodeFromString<Response>(update)
    }

    fun sendMenu(chatId: Long) {
        val urlSendMenu = "$BASE_URL$token/sendMessage"
        val requestMenu = RequestMenu(
            chatId,
            "Основное меню",
            ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyBoard("Учить слова", TgButtonsCallback.LEARN_WORDS.btnDataString),
                        InlineKeyBoard("Статистика", TgButtonsCallback.STATISTICS.btnDataString)
                    ),
                    listOf(InlineKeyBoard("Сбросить прогресс", TgButtonsCallback.CLEAR_PROGRESS.btnDataString))
                )
            )
        )
        val requestBody = json.encodeToString(requestMenu)
        val requestSendMenu = HttpRequest.newBuilder().uri(URI.create(urlSendMenu))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        client.send(requestSendMenu, HttpResponse.BodyHandlers.ofString()).body()
    }

    fun sendQuestion(chatId: Long, question: Question) {
        val urlSendMenu = "$BASE_URL$token/sendMessage"
        val listOfKeyBoard = question.questionAsList.mapIndexed { index, word ->
            listOf(
                InlineKeyBoard(
                    word.translated,
                    "$CALLBACK_DATA_ANSWER_PREFIX$index"
                )
            )
        }.toMutableList()
        listOfKeyBoard.add(listOf(InlineKeyBoard("В меню", "/start")))
        val requestMenu = RequestMenu(
            chatId,
            question.answer.original,
            ReplyMarkup(listOfKeyBoard)
        )
        val requestBody = json.encodeToString(requestMenu)

        val requestSendMenu = HttpRequest.newBuilder().uri(URI.create(urlSendMenu))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        client.send(requestSendMenu, HttpResponse.BodyHandlers.ofString()).body()
    }

}
