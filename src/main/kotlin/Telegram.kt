package org.example

private val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
private val updateIdRegex = "\"update_id\":(.+?),".toRegex()
private val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()
private val dataRegex = "\"callback_data\":\"(.+?)\"".toRegex()

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0
    val trainer = LearnWordsTrainer()
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
        if (msg == "start") {
            getChatId(updates)?.let {
                tgBotService.sendMenu(it)
            }
        }
        if (getData(updates)) {
            getChatId(updates)?.let {
                tgBotService.sendMessage(it, "Выучено 10 из 10 слов 100%")
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

fun getData(updates: String): Boolean {
    val matchResult = dataRegex.find(updates)
    println(updates)
    return matchResult?.groups?.get(1)?.value != null

}
