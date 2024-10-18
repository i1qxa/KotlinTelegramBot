package org.example

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    while (true) {
        Thread.sleep(2000)
        val msg = tgBotService.getMsgText()
        println(msg)
        if (msg == "Hello") tgBotService.sendMessage("Hello")
    }

}
