package org.example

import java.net.http.HttpClient

private val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
private val updateIdRegex = "\"update_id\":(.+?),".toRegex()
private val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()

fun main(args: Array<String>) {

    val client = HttpClient.newBuilder().build()
    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken, client)
    var updateId = 0
    while (true) {
        Thread.sleep(2000)
        val updates = tgBotService.getUpdates(updateId)
        getUpdateId(updates)?.let {
            updateId = it + 1
        }
        val msg = getMsgText(updates)
        println(msg)
        if (msg == "Hello") {
            getChatId(updates)?.let {
                tgBotService.sendMessage(it, "Hello")
            }
        }
    }

}

fun getMsgText(updates: String): String? {
    val matchResult = messageTextRegex.find(updates)
    return matchResult?.groups?.get(1)?.value
}

fun getUpdateId(updates: String): Int? {
    val matchResult = updateIdRegex.find(updates)
    return matchResult?.groups?.get(1)?.value?.toIntOrNull()
}

fun getChatId(updates: String): Int? {
    val matchResult = chatIdRegex.find(updates)
    return matchResult?.groups?.get(1)?.value?.toIntOrNull()
}
