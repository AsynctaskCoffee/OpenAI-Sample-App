package com.openai.chatforall.data

data class MessageData(
    val content: String? = "",
    val imageData: List<DataItem?>? = null,
    val messageType: MessageViewType
)
