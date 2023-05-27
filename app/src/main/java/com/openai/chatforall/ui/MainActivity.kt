package com.openai.chatforall.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import com.openai.chatforall.R
import com.openai.chatforall.base.BaseActivity
import com.openai.chatforall.data.DataItem
import com.openai.chatforall.data.MessageData
import com.openai.chatforall.data.MessageViewType
import com.openai.chatforall.data.OpenAIChatMessagesItem
import com.openai.chatforall.data.OpenAIChatRequest
import com.openai.chatforall.data.OpenAIImageRequest
import com.openai.chatforall.data.RequestType
import com.openai.chatforall.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val messageList = arrayListOf<MessageData>()
    private val mainAdapter by lazy { MainAdapter(messageList) }
    private val viewModel: MainViewModel by viewModels()
    private var selectedRequestType: RequestType = RequestType.GPT
    private lateinit var apiKey: String

    override fun onCreate() {
        apiKey = getString(R.string.apikey)
        setupBinding()
        setupSelectors()
    }

    private fun setupBinding() {
        with(binding) {
            recyclerView.adapter = mainAdapter
            sendBtn.setOnClickListener {
                it.isEnabled = false
                addMessage(etPrompt.text.toString())
                addLoadingState()
                if (selectedRequestType == RequestType.GPT)
                    sendChatRequest()
                else
                    sendImageRequest()
                etPrompt.setText("")
            }
        }
    }

    private fun setupSelectors() {
        with(binding) {
            chatSelector.setOnClickListener {
                toggleRequestType(RequestType.GPT, chatSelector, imageSelector)
            }
            imageSelector.setOnClickListener {
                toggleRequestType(RequestType.DALLE, imageSelector, chatSelector)
            }
        }
    }

    private fun toggleRequestType(requestType: RequestType, active: View, inactive: View) {
        selectedRequestType = requestType
        active.setBackgroundResource(R.drawable.bubble_toggle_selected)
        inactive.setBackgroundResource(R.drawable.bubble_toggle_unselected)
    }

    private fun addLoadingState() {
        messageList.add(
            MessageData(
                messageType = MessageViewType.MESSAGE_LOADING
            )
        )
        mainAdapter.notifyDataSetChanged()
        scrollToBottomAfterMessage()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessage(
        content: String,
        imageData: List<DataItem?>? = null,
        viewType: MessageViewType = MessageViewType.MESSAGE_USER
    ) {
        if (messageList.size > 0 && messageList.last().messageType == MessageViewType.MESSAGE_LOADING)
            messageList.removeLast()
        messageList.add(MessageData(content, imageData, viewType))
        mainAdapter.notifyDataSetChanged()
        scrollToBottomAfterMessage()
        binding.sendBtn.isEnabled = true
    }

    private fun sendChatRequest() {
        viewModel.sendChat(
            apiKey,
            OpenAIChatRequest(
                listOf(
                    OpenAIChatMessagesItem(
                        content = binding.etPrompt.text.toString()
                    )
                )
            )
        )
            .observe(this) {
                it.body()?.choices?.get(0)?.message?.content?.let { content ->
                    addMessage(content, viewType = MessageViewType.MESSAGE_OTHER)
                }
            }
    }

    private fun sendImageRequest() {
        viewModel.sendImage(apiKey, OpenAIImageRequest(prompt = binding.etPrompt.text.toString()))
            .observe(this) {
                it.body()?.data?.let { imageData ->
                    addMessage(
                        content = "",
                        imageData = imageData,
                        viewType = MessageViewType.MESSAGE_OTHER
                    )
                }
            }
    }

    override fun initBinding() = ActivityMainBinding.inflate(layoutInflater)

    private fun scrollToBottomAfterMessage() {
        binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
    }
}