package com.openai.chatforall.data

data class MessageData(
    val content: String? = null,
    val imageData: List<DataItem?>? = null,
    val messageType: MessageViewType
)
