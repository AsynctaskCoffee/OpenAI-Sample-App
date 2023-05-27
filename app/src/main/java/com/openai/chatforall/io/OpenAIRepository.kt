package com.openai.chatforall.io

import com.openai.chatforall.data.OpenAIChatRequest
import com.openai.chatforall.data.OpenAIChatResponse
import com.openai.chatforall.data.OpenAIImageRequest
import com.openai.chatforall.data.OpenAIImageResponse
import retrofit2.Response
import javax.inject.Inject

class OpenAIRepository @Inject constructor(
    private val apiService: OpenAIServices
) {
    suspend fun chatRequest(
        openAiToken: String,
        request: OpenAIChatRequest
    ): Response<OpenAIChatResponse> {
        return apiService.chatRequest("Bearer $openAiToken", request)
    }

    suspend fun imageRequest(
        openAiToken: String,
        request: OpenAIImageRequest
    ): Response<OpenAIImageResponse> {
        return apiService.imageRequest("Bearer $openAiToken", request)
    }
}