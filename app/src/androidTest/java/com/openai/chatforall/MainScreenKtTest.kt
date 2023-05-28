package com.openai.chatforall

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openai.chatforall.data.MessageData
import com.openai.chatforall.data.MessageViewType
import com.openai.chatforall.ui.ButtonRow
import com.openai.chatforall.ui.ChatScreen
import com.openai.chatforall.ui.InputSection
import com.openai.chatforall.ui.LoadingMessage
import com.openai.chatforall.ui.MessageList
import com.openai.chatforall.ui.OtherMessage
import com.openai.chatforall.ui.UserMessage
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun test_ChatScreen_isDisplayed() {
        composeTestRule.setContent {
            ChatScreen(emptyList(), {}, {}, {}, {})
        }

        composeTestRule.onNodeWithTag("ChatScreen").assertIsDisplayed()
    }

    @Test
    fun test_MessageList_isDisplayed() {
        composeTestRule.setContent {
            MessageList(
                listOf(
                    MessageData("Hello", null, MessageViewType.MESSAGE_USER),
                    MessageData("Hello, how can I assist you?", null, MessageViewType.MESSAGE_OTHER),
                    MessageData(messageType = MessageViewType.MESSAGE_LOADING)
                )
            ) {}
        }

        composeTestRule.onNodeWithTag("MessageList").assertIsDisplayed()
    }

    @Test
    fun test_InputSection_isDisplayed() {
        composeTestRule.setContent {
            InputSection { }
        }

        composeTestRule.onNodeWithTag("InputSection").assertIsDisplayed()
    }

    @Test
    fun test_ButtonRow_isDisplayed() {
        composeTestRule.setContent {
            ButtonRow({}, {})
        }

        composeTestRule.onNodeWithTag("ButtonRow").assertIsDisplayed()
    }

    @Test
    fun test_UserMessage_isDisplayed() {
        composeTestRule.setContent {
            UserMessage(MessageData("Test", null, MessageViewType.MESSAGE_USER))
        }

        composeTestRule.onNodeWithTag("UserMessage").assertIsDisplayed()
    }

    @Test
    fun test_OtherMessage_isDisplayed() {
        composeTestRule.setContent {
            OtherMessage(MessageData("Test", null, MessageViewType.MESSAGE_OTHER), {})
        }

        composeTestRule.onNodeWithTag("OtherMessage").assertIsDisplayed()
    }

    @Test
    fun test_LoadingMessage_isDisplayed() {
        composeTestRule.setContent {
            LoadingMessage()
        }

        composeTestRule.onNodeWithTag("LoadingMessage").assertIsDisplayed()
    }

    @Test
    fun testChatScreen() {
        val testMessages = listOf(
            MessageData("Hello", null, MessageViewType.MESSAGE_USER),
            MessageData("Hello, how can I assist you?", null, MessageViewType.MESSAGE_OTHER),
            MessageData(messageType = MessageViewType.MESSAGE_LOADING)
        )

        composeTestRule.setContent {
            ChatScreen(
                messageList = testMessages,
                onGptClicked = {},
                onDallEClicked = {},
                onSendClicked = {},
                downloadImage = {}
            )
        }

        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello, how can I assist you?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Loading").assertIsDisplayed()
    }

}

