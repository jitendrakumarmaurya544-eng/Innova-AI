package com.example.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GenerativeAiRepository
import com.example.data.repository.IGenerativeAiRepository
import com.google.ai.client.generativeai.Chat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Represents a single message in the chat conversation.
 */
data class GenerativeChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val bitmap: Bitmap? = null,
    val isError: Boolean = false
)

/**
 * UI State for the Generative AI interaction screen.
 */
data class GenerativeAiUiState(
    val messages: List<GenerativeChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val currentStreamingResponse: String = "",
    val selectedModel: String = "gemini-3.5-flash",
    val systemInstruction: String = "You are Innova AI, a versatile, creative, and highly helpful assistant.",
    val errorMessage: String? = null,
    val isApiKeyConfigured: Boolean = false
)

/**
 * ViewModel that coordinates sending user messages and receiving AI responses using
 * the Google Generative AI client SDK via [IGenerativeAiRepository].
 */
class GenerativeAiViewModel(
    private val repository: IGenerativeAiRepository = GenerativeAiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GenerativeAiUiState(
            isApiKeyConfigured = repository.isApiKeyConfigured
        )
    )
    val uiState: StateFlow<GenerativeAiUiState> = _uiState.asStateFlow()

    private var currentChatSession: Chat? = null
    private var generationJob: Job? = null

    init {
        initializeChatSession()
    }

    /**
     * Initializes or resets the multi-turn chat session with the selected model and system instruction.
     */
    fun initializeChatSession() {
        val currentState = _uiState.value
        currentChatSession = try {
            repository.startChat(
                modelName = currentState.selectedModel,
                systemInstruction = currentState.systemInstruction
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Sends a user text prompt and receives AI response (either streamed or single-response).
     */
    fun sendMessage(prompt: String, useStreaming: Boolean = true) {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isBlank() || _uiState.value.isGenerating) return

        val userMessage = GenerativeChatMessage(
            text = trimmedPrompt,
            isUser = true
        )

        _uiState.update { current ->
            current.copy(
                messages = current.messages + userMessage,
                isGenerating = true,
                currentStreamingResponse = "",
                errorMessage = null
            )
        }

        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            try {
                if (useStreaming) {
                    val chat = currentChatSession ?: runCatching {
                        repository.startChat(
                            modelName = _uiState.value.selectedModel,
                            systemInstruction = _uiState.value.systemInstruction
                        ).also { currentChatSession = it }
                    }.getOrNull()

                    val streamFlow = if (chat != null) {
                        repository.sendChatMessageStream(chat, trimmedPrompt)
                    } else {
                        repository.sendMessageStream(
                            prompt = trimmedPrompt,
                            modelName = _uiState.value.selectedModel,
                            systemInstruction = _uiState.value.systemInstruction
                        )
                    }

                    val responseBuffer = StringBuilder()

                    streamFlow.collect { chunk ->
                        responseBuffer.append(chunk)
                        _uiState.update { it.copy(currentStreamingResponse = responseBuffer.toString()) }
                    }

                    val finalResponseText = responseBuffer.toString().ifBlank { "No response received." }
                    val assistantMessage = GenerativeChatMessage(
                        text = finalResponseText,
                        isUser = false
                    )

                    _uiState.update { current ->
                        current.copy(
                            messages = current.messages + assistantMessage,
                            isGenerating = false,
                            currentStreamingResponse = ""
                        )
                    }
                } else {
                    val chat = currentChatSession ?: runCatching {
                        repository.startChat(
                            modelName = _uiState.value.selectedModel,
                            systemInstruction = _uiState.value.systemInstruction
                        ).also { currentChatSession = it }
                    }.getOrNull()

                    val result = if (chat != null) {
                        repository.sendChatMessage(chat, trimmedPrompt)
                    } else {
                        repository.sendMessage(
                            prompt = trimmedPrompt,
                            modelName = _uiState.value.selectedModel,
                            systemInstruction = _uiState.value.systemInstruction
                        )
                    }

                    result.onSuccess { responseText ->
                        val assistantMessage = GenerativeChatMessage(
                            text = responseText,
                            isUser = false
                        )
                        _uiState.update { current ->
                            current.copy(
                                messages = current.messages + assistantMessage,
                                isGenerating = false
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = GenerativeChatMessage(
                            text = "Error: ${error.localizedMessage ?: "Failed to generate response"}",
                            isUser = false,
                            isError = true
                        )
                        _uiState.update { current ->
                            current.copy(
                                messages = current.messages + errorMessage,
                                isGenerating = false,
                                errorMessage = error.localizedMessage
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = GenerativeChatMessage(
                    text = "Error: ${e.localizedMessage ?: "Unexpected error occurred"}",
                    isUser = false,
                    isError = true
                )
                _uiState.update { current ->
                    current.copy(
                        messages = current.messages + errorMessage,
                        isGenerating = false,
                        currentStreamingResponse = "",
                        errorMessage = e.localizedMessage
                    )
                }
            }
        }
    }

    /**
     * Sends a multimodal prompt containing both text instructions and an image Bitmap.
     */
    fun sendMessageWithImage(prompt: String, bitmap: Bitmap, useStreaming: Boolean = true) {
        val trimmedPrompt = prompt.trim().ifEmpty { "Analyze this image in detail." }
        if (_uiState.value.isGenerating) return

        val userMessage = GenerativeChatMessage(
            text = trimmedPrompt,
            isUser = true,
            bitmap = bitmap
        )

        _uiState.update { current ->
            current.copy(
                messages = current.messages + userMessage,
                isGenerating = true,
                currentStreamingResponse = "",
                errorMessage = null
            )
        }

        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            try {
                if (useStreaming) {
                    val buffer = StringBuilder()
                    repository.sendMessageWithImageStream(
                        prompt = trimmedPrompt,
                        bitmap = bitmap,
                        modelName = _uiState.value.selectedModel
                    ).collect { chunk ->
                        buffer.append(chunk)
                        _uiState.update { it.copy(currentStreamingResponse = buffer.toString()) }
                    }

                    val finalResponseText = buffer.toString().ifBlank { "No image analysis received." }
                    val assistantMessage = GenerativeChatMessage(
                        text = finalResponseText,
                        isUser = false
                    )

                    _uiState.update { current ->
                        current.copy(
                            messages = current.messages + assistantMessage,
                            isGenerating = false,
                            currentStreamingResponse = ""
                        )
                    }
                } else {
                    val result = repository.sendMessageWithImage(
                        prompt = trimmedPrompt,
                        bitmap = bitmap,
                        modelName = _uiState.value.selectedModel
                    )

                    result.onSuccess { responseText ->
                        val assistantMessage = GenerativeChatMessage(
                            text = responseText,
                            isUser = false
                        )
                        _uiState.update { current ->
                            current.copy(
                                messages = current.messages + assistantMessage,
                                isGenerating = false
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = GenerativeChatMessage(
                            text = "Error: ${error.localizedMessage ?: "Failed to analyze image"}",
                            isUser = false,
                            isError = true
                        )
                        _uiState.update { current ->
                            current.copy(
                                messages = current.messages + errorMessage,
                                isGenerating = false,
                                errorMessage = error.localizedMessage
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isGenerating = false,
                        currentStreamingResponse = "",
                        errorMessage = e.localizedMessage
                    )
                }
            }
        }
    }

    /**
     * Retries the last user message in the conversation.
     */
    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.isUser } ?: return
        if (lastUserMessage.bitmap != null) {
            sendMessageWithImage(lastUserMessage.text, lastUserMessage.bitmap)
        } else {
            sendMessage(lastUserMessage.text)
        }
    }

    /**
     * Clears all chat messages and resets the conversation session.
     */
    fun clearChat() {
        generationJob?.cancel()
        _uiState.update { current ->
            current.copy(
                messages = emptyList(),
                isGenerating = false,
                currentStreamingResponse = "",
                errorMessage = null
            )
        }
        initializeChatSession()
    }

    /**
     * Updates the model being used (e.g. "gemini-3.5-flash" or "gemini-3.1-pro-preview").
     */
    fun setModel(modelName: String) {
        if (_uiState.value.selectedModel == modelName) return
        _uiState.update { it.copy(selectedModel = modelName) }
        initializeChatSession()
    }

    /**
     * Updates the system instruction for the AI agent persona.
     */
    fun setSystemInstruction(instruction: String) {
        _uiState.update { it.copy(systemInstruction = instruction) }
        initializeChatSession()
    }

    /**
     * Dismisses active error messages.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        generationJob?.cancel()
    }
}
