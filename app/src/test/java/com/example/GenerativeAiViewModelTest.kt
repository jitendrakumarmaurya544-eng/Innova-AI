package com.example

import android.graphics.Bitmap
import com.example.data.repository.IGenerativeAiRepository
import com.example.viewmodel.GenerativeAiViewModel
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenerativeAiViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeGenerativeAiRepository : IGenerativeAiRepository {
        override val isApiKeyConfigured: Boolean = true
        var lastSentPrompt: String? = null
        var simulatedResponse: String = "Hello, I am Gemini AI!"

        override fun createGenerativeModel(modelName: String, systemInstruction: String?, temperature: Float): GenerativeModel {
            throw UnsupportedOperationException("Not needed in fake test")
        }

        override fun startChat(modelName: String, systemInstruction: String?, initialHistory: List<Content>): Chat {
            // Return null or mocked in unit test, but ViewModel guards chat calls
            // In unit tests, we stub chat or can mock via reflection or interface methods
            return null as? Chat ?: throw UnsupportedOperationException("Chat session mock")
        }

        override suspend fun sendMessage(prompt: String, modelName: String, systemInstruction: String?): Result<String> {
            lastSentPrompt = prompt
            return Result.success(simulatedResponse)
        }

        override fun sendMessageStream(prompt: String, modelName: String, systemInstruction: String?): Flow<String> {
            lastSentPrompt = prompt
            return flowOf("Streamed ", "AI ", "Response")
        }

        override suspend fun sendChatMessage(chat: Chat, prompt: String): Result<String> {
            lastSentPrompt = prompt
            return Result.success(simulatedResponse)
        }

        override fun sendChatMessageStream(chat: Chat, prompt: String): Flow<String> {
            lastSentPrompt = prompt
            return flowOf("Streamed ", "AI ", "Response")
        }

        override suspend fun sendMessageWithImage(prompt: String, bitmap: Bitmap, modelName: String): Result<String> {
            lastSentPrompt = prompt
            return Result.success("Image analysis: $simulatedResponse")
        }

        override fun sendMessageWithImageStream(prompt: String, bitmap: Bitmap, modelName: String): Flow<String> {
            lastSentPrompt = prompt
            return flowOf("Image ", "analysis: ", simulatedResponse)
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val fakeRepo = FakeGenerativeAiRepository()
        val viewModel = GenerativeAiViewModel(fakeRepo)

        val state = viewModel.uiState.value
        assertTrue(state.messages.isEmpty())
        assertFalse(state.isGenerating)
        assertEquals("gemini-3.5-flash", state.selectedModel)
        assertTrue(state.isApiKeyConfigured)
    }

    @Test
    fun testSendMessage_updatesStateAndReceivesResponse() = runTest {
        val fakeRepo = FakeGenerativeAiRepository()
        val viewModel = GenerativeAiViewModel(fakeRepo)

        viewModel.sendMessage("Hello Gemini", useStreaming = true)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isGenerating)
        assertEquals(2, state.messages.size)
        assertEquals("Hello Gemini", state.messages[0].text)
        assertTrue(state.messages[0].isUser)
        assertEquals("Streamed AI Response", state.messages[1].text)
        assertFalse(state.messages[1].isUser)
    }

    @Test
    fun testSendMessage_nonStreaming() = runTest {
        val fakeRepo = FakeGenerativeAiRepository()
        val viewModel = GenerativeAiViewModel(fakeRepo)

        viewModel.sendMessage("What is Kotlin?", useStreaming = false)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.messages.size)
        assertEquals("What is Kotlin?", state.messages[0].text)
        assertEquals("Hello, I am Gemini AI!", state.messages[1].text)
    }

    @Test
    fun testClearChat() = runTest {
        val fakeRepo = FakeGenerativeAiRepository()
        val viewModel = GenerativeAiViewModel(fakeRepo)

        viewModel.sendMessage("Test message", useStreaming = false)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.messages.size)

        viewModel.clearChat()
        assertEquals(0, viewModel.uiState.value.messages.size)
    }

    @Test
    fun testSetModel() {
        val fakeRepo = FakeGenerativeAiRepository()
        val viewModel = GenerativeAiViewModel(fakeRepo)

        viewModel.setModel("gemini-3.1-pro-preview")
        assertEquals("gemini-3.1-pro-preview", viewModel.uiState.value.selectedModel)
    }
}
