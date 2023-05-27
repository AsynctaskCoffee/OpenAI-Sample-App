package com.openai.chatforall.ui

import androidx.lifecycle.liveData
import com.openai.chatforall.base.BaseViewModel
import com.openai.chatforall.data.OpenAIChatRequest
import com.openai.chatforall.data.OpenAIImageRequest
import com.openai.chatforall.io.OpenAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: OpenAIRepository
) : BaseViewModel() {

    fun sendChat(openAiToken: String, request: OpenAIChatRequest) = liveData {
        val result = repository.chatRequest(openAiToken, request)
        emit(result)
    }

    fun sendImage(openAiToken: String, request: OpenAIImageRequest) = liveData {
        val result = repository.imageRequest(openAiToken, request)
        emit(result)
    }

}