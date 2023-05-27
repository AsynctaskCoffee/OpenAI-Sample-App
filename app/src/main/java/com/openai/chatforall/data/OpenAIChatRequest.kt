package com.openai.chatforall.data

import com.google.gson.annotations.SerializedName

data class OpenAIChatRequest(
    @field:SerializedName("messages")
    val messages: List<OpenAIChatMessagesItem?>? = null,
    @field:SerializedName("model")
    val model: String = "gpt-3.5-turbo"
)

data class OpenAIChatMessagesItem(
    @field:SerializedName("role")
    val role: String = "assistant",
    @field:SerializedName("content")
    val content: String? = null
)
