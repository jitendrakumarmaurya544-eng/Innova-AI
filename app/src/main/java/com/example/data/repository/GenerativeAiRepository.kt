package com.example.data.repository

import android.graphics.Bitmap
import com.example.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository interface defining Google Generative AI client interactions.
 */
interface IGenerativeAiRepository {
    val isApiKeyConfigured: Boolean
    fun createGenerativeModel(modelName: String = "gemini-3.5-flash", systemInstruction: String? = null, temperature: Float = 0.7f): GenerativeModel
    fun startChat(modelName: String = "gemini-3.5-flash", systemInstruction: String? = null, initialHistory: List<Content> = emptyList()): Chat
    suspend fun sendMessage(prompt: String, modelName: String = "gemini-3.5-flash", systemInstruction: String? = null): Result<String>
    fun sendMessageStream(prompt: String, modelName: String = "gemini-3.5-flash", systemInstruction: String? = null): Flow<String>
    suspend fun sendChatMessage(chat: Chat, prompt: String): Result<String>
    fun sendChatMessageStream(chat: Chat, prompt: String): Flow<String>
    suspend fun sendMessageWithImage(prompt: String, bitmap: Bitmap, modelName: String = "gemini-3.5-flash"): Result<String>
    fun sendMessageWithImageStream(prompt: String, bitmap: Bitmap, modelName: String = "gemini-3.5-flash"): Flow<String>
}

/**
 * Repository that utilizes the official Google Generative AI client SDK (`com.google.ai.client.generativeai`)
 * to send user messages, stream responses, maintain multi-turn chat sessions, and process multimodal inputs.
 */
class GenerativeAiRepository(
    private val defaultModelName: String = "gemini-3.5-flash",
    private val apiKeyProvider: () -> String = {
        BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "MY_GEMINI_API_KEY" } ?: ""
    }
) : IGenerativeAiRepository {

    override val isApiKeyConfigured: Boolean
        get() = apiKeyProvider().isNotEmpty()

    /**
     * Instantiates a configured [GenerativeModel] with safety thresholds and generation parameters.
     */
    override fun createGenerativeModel(
        modelName: String,
        systemInstruction: String?,
        temperature: Float
    ): GenerativeModel {
        val apiKey = apiKeyProvider()
        val config = generationConfig {
            this.temperature = temperature
            this.topK = 40
            this.topP = 0.95f
        }

        val safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )

        val sysContent = systemInstruction?.takeIf { it.isNotBlank() }?.let { instruction ->
            content {
                text(instruction)
            }
        }

        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey,
            generationConfig = config,
            safetySettings = safetySettings,
            systemInstruction = sysContent
        )
    }

    /**
     * Initiates a multi-turn chat conversation using the Google Generative AI client.
     */
    override fun startChat(
        modelName: String,
        systemInstruction: String?,
        initialHistory: List<Content>
    ): Chat {
        val model = createGenerativeModel(modelName, systemInstruction)
        return model.startChat(history = initialHistory)
    }

    /**
     * Sends a single text message and returns the complete AI response text.
     */
    override suspend fun sendMessage(
        prompt: String,
        modelName: String,
        systemInstruction: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext Result.success(
                "⚠️ **API Key Notice**: Please configure your `GEMINI_API_KEY` in the AI Studio Secrets panel.\n\n" +
                "Preview response for: \"$prompt\"\n" +
                "The Google Generative AI client is ready to receive live responses as soon as your key is active."
            )
        }

        try {
            val model = createGenerativeModel(modelName, systemInstruction)
            val response = model.generateContent(prompt)
            val text = response.text ?: "No response generated."
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sends a single text message and returns a real-time reactive [Flow] of text chunks as they are generated.
     */
    override fun sendMessageStream(
        prompt: String,
        modelName: String,
        systemInstruction: String?
    ): Flow<String> = flow {
        if (!isApiKeyConfigured) {
            emit("⚠️ Please set your `GEMINI_API_KEY` in the Secrets panel to enable live streaming.")
            return@flow
        }

        try {
            val model = createGenerativeModel(modelName, systemInstruction)
            model.generateContentStream(prompt).collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            emit("\n\n❌ **Error communicating with Gemini**: ${e.localizedMessage ?: e.message}")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Sends a message within an existing [Chat] session, preserving conversation history.
     */
    override suspend fun sendChatMessage(chat: Chat, prompt: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext Result.success(
                "Preview response for: \"$prompt\". (Add `GEMINI_API_KEY` to connect live chat session)."
            )
        }

        try {
            val response = chat.sendMessage(prompt)
            Result.success(response.text ?: "No response text.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Streams a message within an active multi-turn [Chat] session.
     */
    override fun sendChatMessageStream(chat: Chat, prompt: String): Flow<String> = flow {
        if (!isApiKeyConfigured) {
            emit("⚠️ Add `GEMINI_API_KEY` to connect live chat stream.")
            return@flow
        }

        try {
            chat.sendMessageStream(prompt).collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            emit("\n\n❌ Error: ${e.localizedMessage ?: e.message}")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Sends a multimodal request containing both text and a Bitmap image.
     */
    override suspend fun sendMessageWithImage(
        prompt: String,
        bitmap: Bitmap,
        modelName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext Result.success(
                "Multimodal image analysis preview for: \"$prompt\". Set your API key for neural vision processing."
            )
        }

        try {
            val model = createGenerativeModel(modelName)
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }
            val response = model.generateContent(inputContent)
            Result.success(response.text ?: "No analysis returned.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Streams a multimodal request containing both text and an image.
     */
    override fun sendMessageWithImageStream(
        prompt: String,
        bitmap: Bitmap,
        modelName: String
    ): Flow<String> = flow {
        if (!isApiKeyConfigured) {
            emit("⚠️ Please set your `GEMINI_API_KEY` to stream image analysis.")
            return@flow
        }

        try {
            val model = createGenerativeModel(modelName)
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }
            model.generateContentStream(inputContent).collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            emit("\n\n❌ Error analyzing image: ${e.localizedMessage ?: e.message}")
        }
    }.flowOn(Dispatchers.IO)
}
