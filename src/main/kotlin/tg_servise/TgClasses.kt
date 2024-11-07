package org.example.tg_servise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("message_id")
    val messageId: Long,
    @SerialName("chat")
    val chat: Chat,
    @SerialName("text")
    val text: String,
)

@Serializable
data class Chat(
    @SerialName("id")
    val chatId: Long,
)

@Serializable
data class CallbackQuery(
    @SerialName("message")
    val message: Message? = null,
    @SerialName("data")
    val data: String,
)

@Serializable
data class RequestMenu(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyBoard: List<List<InlineKeyBoard>>,
)

@Serializable
data class InlineKeyBoard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callBackData: String,
)