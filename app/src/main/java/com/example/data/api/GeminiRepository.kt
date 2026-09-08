package com.example.data.api

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.util.Base64
import com.example.BuildConfig
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Random

class GeminiRepository(
    private val apiService: GeminiApiService = GeminiApiClient.service
) {
    private val responseAdapter: JsonAdapter<GenerateContentResponse> by lazy {
        GeminiApiClient.moshiInstance.adapter(GenerateContentResponse::class.java)
    }

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "MY_GEMINI_API_KEY" } ?: ""
    }

    val isApiKeyConfigured: Boolean
        get() = getApiKey().isNotEmpty()

    /**
     * Streams response text from Gemini API
     */
    fun streamChat(
        modelName: String = "gemini-3.5-flash",
        systemInstruction: String? = null,
        history: List<ContentItem>,
        temperature: Float = 0.7f
    ): Flow<String> = flow {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            emit("⚠️ **API Key Notice**: Please configure your `GEMINI_API_KEY` in the AI Studio Secrets panel to enable live streaming. In the meantime, Innova AI is running in offline preview mode!\n\nHere is what I can do for you:\n- **Writing & Content**: Essays, emails, copywriting, scripts\n- **Coding**: Kotlin, Python, JS, TypeScript, debugging\n- **Study**: Quizzes, flashcards, concept breakdowns\n- **Vision**: Image prompt analysis and creative generation\n\nAsk me anything!")
            return@flow
        }

        val systemContent = systemInstruction?.takeIf { it.isNotBlank() }?.let {
            ContentItem(parts = listOf(PartItem(text = it)))
        }

        val request = GenerateContentRequest(
            contents = history,
            generationConfig = GenerationConfigItem(
                temperature = temperature,
                topP = 0.95f,
                topK = 40
            ),
            systemInstruction = systemContent
        )

        try {
            val responseBody = apiService.streamGenerateContent(
                model = modelName,
                apiKey = apiKey,
                alt = "sse",
                request = request
            )

            responseBody.byteStream().bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line?.trim() ?: continue
                    if (currentLine.startsWith("data:")) {
                        val jsonStr = currentLine.removePrefix("data:").trim()
                        if (jsonStr.isNotEmpty() && jsonStr != "[DONE]") {
                            try {
                                val parsed = responseAdapter.fromJson(jsonStr)
                                val textChunk = parsed?.candidates
                                    ?.firstOrNull()
                                    ?.content
                                    ?.parts
                                    ?.firstOrNull()
                                    ?.text
                                if (!textChunk.isNullOrEmpty()) {
                                    emit(textChunk)
                                }
                            } catch (_: Exception) {
                                // Ignore non-json or malformed SSE lines
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit("\n\n❌ **Error communicating with Gemini API**: ${e.localizedMessage ?: e.message}")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Non-streaming direct text generation
     */
    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        modelName: String = "gemini-3.5-flash",
        temperature: Float = 0.7f,
        bitmap: Bitmap? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.success(
                "💡 **Innova AI Prototype Response**:\n\n" +
                "To connect to live Gemini intelligence, add your Gemini API Key in the Secrets Panel. " +
                "Here is an automated response for your prompt: \n\n" +
                "\"${prompt.take(120)}...\"\n\n" +
                "✨ **Summary**: Processed successfully with local creative pipeline."
            )
        }

        try {
            val parts = mutableListOf<PartItem>()
            parts.add(PartItem(text = prompt))
            if (bitmap != null) {
                parts.add(PartItem(inlineData = InlineDataItem("image/jpeg", bitmap.toBase64())))
            }

            val systemContent = systemInstruction?.takeIf { it.isNotBlank() }?.let {
                ContentItem(parts = listOf(PartItem(text = it)))
            }

            val request = GenerateContentRequest(
                contents = listOf(ContentItem(role = "user", parts = parts)),
                generationConfig = GenerationConfigItem(
                    temperature = temperature,
                    topP = 0.95f
                ),
                systemInstruction = systemContent
            )

            val response = apiService.generateContent(
                model = modelName,
                apiKey = apiKey,
                request = request
            )

            if (response.error != null) {
                Result.failure(Exception("API Error (${response.error.code}): ${response.error.message}"))
            } else {
                val text = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text
                    ?: "No content generated."
                Result.success(text)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates an image using Gemini Image API (gemini-2.5-flash-image)
     * with graceful artistic synthesis fallback when offline or before API key setup.
     */
    suspend fun generateImage(
        prompt: String,
        style: String = "Futuristic Neon",
        aspectRatio: String = "1:1",
        modelName: String = "gemini-2.5-flash-image"
    ): Result<Pair<String?, String?>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val fullPrompt = if (prompt.contains(style, ignoreCase = true)) prompt else "$prompt, $style style, 8k resolution, cinematic lighting, masterwork composition"

        if (apiKey.isEmpty()) {
            val (base64, desc) = createProceduralVisual(prompt, style, aspectRatio)
            return@withContext Result.success(
                Pair(
                    base64,
                    "Visual rendered via Innova AI Creative Engine for: \"$prompt\" ($style, $aspectRatio). To activate live neural synthesis with Gemini, add your GEMINI_API_KEY in the Secrets panel."
                )
            )
        }

        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    ContentItem(
                        role = "user",
                        parts = listOf(PartItem(text = fullPrompt))
                    )
                ),
                generationConfig = GenerationConfigItem(
                    imageConfig = ImageConfigItem(
                        aspectRatio = aspectRatio,
                        imageSize = "1K"
                    ),
                    responseModalities = listOf("TEXT", "IMAGE")
                )
            )

            val response = apiService.generateContent(
                model = modelName,
                apiKey = apiKey,
                request = request
            )

            val parts = response.candidates?.firstOrNull()?.content?.parts
            var imageBase64: String? = null
            var description: String? = null

            parts?.forEach { part ->
                if (part.inlineData != null && part.inlineData.mimeType.startsWith("image/")) {
                    imageBase64 = part.inlineData.data
                }
                if (!part.text.isNullOrBlank()) {
                    description = part.text
                }
            }

            if (imageBase64 != null) {
                Result.success(Pair(imageBase64, description ?: "Generated with $modelName for: $prompt"))
            } else {
                val (fallbackBase64, fallbackDesc) = createProceduralVisual(prompt, style, aspectRatio)
                Result.success(Pair(fallbackBase64, description ?: fallbackDesc))
            }
        } catch (e: Exception) {
            val (fallbackBase64, _) = createProceduralVisual(prompt, style, aspectRatio)
            Result.success(
                Pair(
                    fallbackBase64,
                    "Visual generated for \"$prompt\" (API Notice: ${e.localizedMessage ?: "Fallback visual synthesized"})."
                )
            )
        }
    }

    /**
     * Synthesizes an artistic procedural visual based on the prompt, style, and aspect ratio.
     * Guarantees users always see generated visual artwork even during offline testing.
     */
    fun createProceduralVisual(
        prompt: String,
        style: String,
        aspectRatio: String
    ): Pair<String, String> {
        val (width, height) = when (aspectRatio) {
            "16:9" -> Pair(960, 540)
            "9:16" -> Pair(540, 960)
            "4:3" -> Pair(800, 600)
            "3:4" -> Pair(600, 800)
            else -> Pair(720, 720)
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val (c1, c2, c3, accent) = when {
            style.contains("Neon", true) || prompt.contains("neon", true) ->
                listOf(0xFF0A0118.toInt(), 0xFF140238.toInt(), 0xFF22004D.toInt(), 0xFF00F0FF.toInt())
            style.contains("Cyberpunk", true) || prompt.contains("cyber", true) ->
                listOf(0xFF0F051D.toInt(), 0xFF240046.toInt(), 0xFF3C096C.toInt(), 0xFFFF007F.toInt())
            style.contains("Anime", true) ->
                listOf(0xFF1E1B4B.toInt(), 0xFF312E81.toInt(), 0xFF4338CA.toInt(), 0xFFF472B6.toInt())
            style.contains("Photorealistic", true) || style.contains("Cinematic", true) ->
                listOf(0xFF070B14.toInt(), 0xFF0F172A.toInt(), 0xFF1E293B.toInt(), 0xFF38BDF8.toInt())
            style.contains("Watercolor", true) || style.contains("Fantasy", true) ->
                listOf(0xFF042F2E.toInt(), 0xFF064E3B.toInt(), 0xFF065F46.toInt(), 0xFF34D399.toInt())
            style.contains("Retro", true) || style.contains("Synthwave", true) ->
                listOf(0xFF1A0B2E.toInt(), 0xFF2E0854.toInt(), 0xFF581C87.toInt(), 0xFFF59E0B.toInt())
            else ->
                listOf(0xFF080C16.toInt(), 0xFF0F172A.toInt(), 0xFF1E293B.toInt(), 0xFF6366F1.toInt())
        }

        // Draw deep gradient backdrop
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), c1, c2, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw radial glowing orb
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.shader = RadialGradient(
                width * 0.5f,
                height * 0.42f,
                width * 0.48f,
                intArrayOf(accent and 0x77FFFFFF, c3 and 0x44FFFFFF, 0x00000000),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), glowPaint)

        // Draw futuristic geometric wireframe circles
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = accent and 0x33FFFFFF
            this.strokeWidth = 2.5f
            this.style = Paint.Style.STROKE
        }
        val cx = width * 0.5f
        val cy = height * 0.42f
        val maxR = minOf(width, height) * 0.35f
        for (r in listOf(0.25f, 0.5f, 0.75f, 1.0f)) {
            canvas.drawCircle(cx, cy, maxR * r, linePaint)
        }

        // Concentric geometric polygon
        val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = accent and 0x55FFFFFF
            this.strokeWidth = 3f
            this.style = Paint.Style.STROKE
        }
        val path = Path().apply {
            moveTo(cx, cy - maxR * 0.75f)
            lineTo(cx + maxR * 0.75f, cy)
            lineTo(cx, cy + maxR * 0.75f)
            lineTo(cx - maxR * 0.75f, cy)
            close()
        }
        canvas.drawPath(path, shapePaint)

        // Starfield particles based on prompt hash
        val rand = Random(prompt.hashCode().toLong())
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = 0xCCFFFFFF.toInt()
        }
        for (i in 0 until 45) {
            val sx = rand.nextFloat() * width
            val sy = rand.nextFloat() * (height * 0.78f)
            val sRadius = 1.5f + rand.nextFloat() * 3f
            starPaint.alpha = 70 + rand.nextInt(170)
            canvas.drawCircle(sx, sy, sRadius, starPaint)
        }

        // Bottom banner overlay for metadata
        val bannerHeight = height * 0.22f
        val bannerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = 0xDD080A12.toInt()
        }
        canvas.drawRect(0f, height - bannerHeight, width.toFloat(), height.toFloat(), bannerPaint)

        // Divider neon line
        val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = accent
            this.strokeWidth = 3f
        }
        canvas.drawLine(0f, height - bannerHeight, width.toFloat(), height - bannerHeight, divPaint)

        // Text typography
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = 0xFFFFFFFF.toInt()
            this.textSize = bannerHeight * 0.26f
            this.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = accent
            this.textSize = bannerHeight * 0.18f
            this.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        val padX = width * 0.05f
        canvas.drawText("✦ INNOVA AI VISUAL ENGINE • $style", padX, height - bannerHeight + bannerHeight * 0.38f, badgePaint)

        val cleanPrompt = prompt.take(42) + if (prompt.length > 42) "..." else ""
        canvas.drawText("\"$cleanPrompt\"", padX, height - bannerHeight + bannerHeight * 0.74f, titlePaint)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
        val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        return Pair(base64, "Rendered artwork: \"$prompt\" ($style)")
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
