package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"
    val client = HttpClient.newBuilder().build()
    val requestGetMe = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    client.send(requestGetMe, HttpResponse.BodyHandlers.ofString()).apply {
        println("GetMe:\n${this.body()}")
    }
    client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString()).apply {
        println("GetUpdates:\n${this.body()}")
    }

}