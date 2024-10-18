package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val token: String) {

    private val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    private val updateIdRegex = "\"update_id\":(.+?),".toRegex()
    private val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()

    private var updates: String = ""
    private var updateId = 0
    private var chatId = 0

    fun getMsgText(): String? {
        getUpdates()
        val matchResult = messageTextRegex.find(updates)
        return matchResult?.groups?.get(1)?.value
    }

    fun sendMessage(text: String) {
        val urlSendMessage = "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$text"
        val client = HttpClient.newBuilder().build()
        val requestSendMessage = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString()).body()
    }

    private fun getUpdates() {
        val urlGetUpdates = "https://api.telegram.org/bot$token/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        updates = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).body()
        fetchUpdateId()
        fetchChatId()
    }

    private fun fetchUpdateId() {
        val matchResult = updateIdRegex.find(updates)
        matchResult?.groups?.get(1)?.value?.toIntOrNull()?.let {
            updateId = it + 1
        }
    }

    private fun fetchChatId() {
        val matchResult = chatIdRegex.find(updates)
        matchResult?.groups?.get(1)?.value?.toIntOrNull()?.let {
            chatId = it
        }
    }


}