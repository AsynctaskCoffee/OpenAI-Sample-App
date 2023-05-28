package com.openai.chatforall.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openai.chatforall.data.MessageData
import com.openai.chatforall.data.MessageViewType
import com.openai.chatforall.data.OpenAIChatMessagesItem
import com.openai.chatforall.data.OpenAIChatRequest
import com.openai.chatforall.data.OpenAIImageRequest
import com.openai.chatforall.data.RequestType
import com.openai.chatforall.io.OpenAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: OpenAIRepository
) : ViewModel() {

    var selectedRequestType by mutableStateOf(RequestType.GPT)
    var messageList = mutableStateOf<List<MessageData>>(listOf())
    fun sendRequest(openAiToken: String, prompt: String) {
        if (selectedRequestType == RequestType.GPT) {
            sendChat(
                openAiToken,
                OpenAIChatRequest(listOf(OpenAIChatMessagesItem(content = prompt)))
            )
        } else {
            sendImage(openAiToken, OpenAIImageRequest(prompt = prompt))
        }
    }

    private fun sendChat(openAiToken: String, request: OpenAIChatRequest) {
        addMessage(
            MessageData(
                content = request.messages?.get(0)?.content,
                messageType = MessageViewType.MESSAGE_USER
            )
        )
        viewModelScope.launch {
            addMessage(MessageData(messageType = MessageViewType.MESSAGE_LOADING))
            val result = repository.chatRequest(openAiToken, request)
            removeLoadingStateIfExist()
            addMessage(
                MessageData(
                    content = result.body()?.choices?.get(0)?.message?.content,
                    messageType = MessageViewType.MESSAGE_OTHER
                )
            )
        }
    }

    private fun sendImage(openAiToken: String, request: OpenAIImageRequest) {
        addMessage(
            MessageData(
                content = request.prompt,
                messageType = MessageViewType.MESSAGE_USER
            )
        )
        viewModelScope.launch {
            addMessage(MessageData(messageType = MessageViewType.MESSAGE_LOADING))
            val result = repository.imageRequest(openAiToken, request)
            removeLoadingStateIfExist()
            addMessage(
                MessageData(
                    imageData = result.body()?.data,
                    messageType = MessageViewType.MESSAGE_OTHER
                )
            )
        }
    }

    private fun addMessage(messageData: MessageData) {
        messageList.value = messageList.value + messageData
    }

    private fun removeLoadingStateIfExist() {
        if (messageList.value.isNotEmpty() && messageList.value.last().messageType == MessageViewType.MESSAGE_LOADING) {
            messageList.value = messageList.value.dropLast(1)
        }
    }
}

