package com.openai.chatforall.io

import com.openai.chatforall.data.OpenAIChatRequest
import com.openai.chatforall.data.OpenAIChatResponse
import com.openai.chatforall.data.OpenAIImageRequest
import com.openai.chatforall.data.OpenAIImageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIServices {
    @POST("/v1/chat/completions")
    suspend fun chatRequest(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAIChatRequest
    ): Response<OpenAIChatResponse>

    @POST("/v1/images/generations")
    suspend fun imageRequest(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAIImageRequest
    ): Response<OpenAIImageResponse>
}
