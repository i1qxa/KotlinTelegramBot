package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private const val BASE_URL = "https://api.telegram.org/bot"

class TelegramBotService(private val token: String, private val client: HttpClient) {

    fun sendMessage(chatId: Int, text: String) {
        val urlSendMessage = "$BASE_URL$token/sendMessage?chat_id=$chatId&text=$text"
        val requestSendMessage = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString()).body()
    }

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$BASE_URL$token/getUpdates?offset=$updateId"
        val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        return client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).body()
    }


}