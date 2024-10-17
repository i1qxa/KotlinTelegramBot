package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId)
        println(getMsgText(updates))
        getUpdateId(updates).also {
            if (it != null) {
                updateId = it + 1
            }
        }
    }

}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    return client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).body()
}

fun getMsgText(updates: String): String? {
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val matchResult = messageTextRegex.find(updates)
    return matchResult?.groups?.get(1)?.value
}


fun getUpdateId(updates: String): Int? {
    val updateIdRegex = "\"update_id\":(.+?),".toRegex()
    val matchResult = updateIdRegex.find(updates)
    return matchResult?.groups?.get(1)?.value?.toIntOrNull()
}
