package org.example.tg_servise

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

private const val BASE_URL = "https://api.telegram.org/bot"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"

class TelegramBotService(private val token: String) {

    private val client = HttpClient.newBuilder().build()

    fun sendMessage(chatId: Int, text: String) {
        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )
        val urlSendMessage = "$BASE_URL$token/sendMessage?chat_id=$chatId&text=$encoded"
        val requestSendMessage = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString()).body()
    }

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$BASE_URL$token/getUpdates?offset=$updateId"
        val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        return client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).body()
    }

    fun sendMenu(chatId: Int) {
        val urlSendMenu = "$BASE_URL$token/sendMessage"
        println(TgButtons.STATISTICS)
        val sendMenuBody = """
            {
                "chat_id":$chatId,
                "text":"Основное меню",
                "reply_markup":{
                    "inline_keyboard":[
                        [
                            {    
                            "text":"Учить слова",
                            "callback_data":"${TgButtons.LEARN_WORDS.btnDataString}"
                            },
                            {
                            "text":"Статистика",
                            "callback_data":"${TgButtons.STATISTICS.btnDataString}"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val requestSendMenu = HttpRequest.newBuilder().uri(URI.create(urlSendMenu))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        client.send(requestSendMenu, HttpResponse.BodyHandlers.ofString()).body()
    }

}