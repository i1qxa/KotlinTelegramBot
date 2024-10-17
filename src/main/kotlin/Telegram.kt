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
        println(updates)
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
    client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).apply {
        return ("GetUpdates:\n${this.body()}")
    }
}

fun getUpdateId(updates: String): Int? {
    val startUpdateId = updates.lastIndexOf("update_id")
    val endUpdateId = updates.lastIndexOf(",\n\"message\"")
    if (startUpdateId == -1 || endUpdateId == -1) return null
    return updates.substring(startUpdateId + 11, endUpdateId).toIntOrNull()
}