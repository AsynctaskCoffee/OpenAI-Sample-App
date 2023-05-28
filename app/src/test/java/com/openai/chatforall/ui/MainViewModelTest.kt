package com.openai.chatforall.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openai.chatforall.data.ChoicesItem
import com.openai.chatforall.data.DataItem
import com.openai.chatforall.data.Message
import com.openai.chatforall.data.MessageData
import com.openai.chatforall.data.MessageViewType
import com.openai.chatforall.data.OpenAIChatMessagesItem
import com.openai.chatforall.data.OpenAIChatRequest
import com.openai.chatforall.data.OpenAIChatResponse
import com.openai.chatforall.data.OpenAIImageRequest
import com.openai.chatforall.data.OpenAIImageResponse
import com.openai.chatforall.data.RequestType
import com.openai.chatforall.io.OpenAIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import retrofit2.Response

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mockRepository: OpenAIRepository

    @InjectMocks
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_sendRequest_with_GPT_and_image() = mainCoroutineRule.runBlockingTest {
        val expectedChatRequest =
            OpenAIChatRequest(listOf(OpenAIChatMessagesItem(content = "test prompt")))
        val expectedChatResponse = OpenAIChatResponse(
            choices = listOf(ChoicesItem(message = Message(content = "response content")))
        )
        val expectedImageRequest = OpenAIImageRequest(prompt = "test prompt")
        val expectedImageResponse = OpenAIImageResponse(data = listOf(DataItem("test url")))

        `when`(
            mockRepository.chatRequest(anyString(), argThat { this == expectedChatRequest })
        ).thenReturn(
            Response.success(expectedChatResponse)
        )
        `when`(
            mockRepository.imageRequest(anyString(), argThat { this == expectedImageRequest })
        ).thenReturn(
            Response.success(expectedImageResponse)
        )

        // Test sendRequest with GPT
        viewModel.sendRequest("test token", "test prompt")
        verify(mockRepository).chatRequest(anyString(), argThat { this == expectedChatRequest })
        assertEquals(
            listOf(
                MessageData(content = "test prompt", messageType = MessageViewType.MESSAGE_USER),
                MessageData(
                    content = "response content", messageType = MessageViewType.MESSAGE_OTHER
                )
            ), viewModel.messageList.value
        )

        // Reset messageList
        viewModel.messageList.value = listOf()

        // Test sendRequest with image
        viewModel.selectedRequestType = RequestType.DALLE
        viewModel.sendRequest("test token", "test prompt")
        verify(mockRepository).imageRequest(anyString(), argThat { this == expectedImageRequest })
        assertEquals(
            listOf(
                MessageData(content = "test prompt", messageType = MessageViewType.MESSAGE_USER),
                MessageData(
                    imageData = listOf(DataItem("test url")),
                    messageType = MessageViewType.MESSAGE_OTHER
                )
            ), viewModel.messageList.value
        )
    }
}

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
