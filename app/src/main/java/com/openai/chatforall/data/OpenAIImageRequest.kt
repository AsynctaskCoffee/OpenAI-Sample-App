package com.openai.chatforall.data

import com.google.gson.annotations.SerializedName

data class OpenAIImageRequest(
    @field:SerializedName("size")
    val size: String = "512x512",
    @field:SerializedName("prompt")
    val prompt: String? = null,
    @field:SerializedName("n")
    val n: Int = 1
)
