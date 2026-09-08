package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<ContentItem>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfigItem? = null,
    @Json(name = "systemInstruction") val systemInstruction: ContentItem? = null
)

@JsonClass(generateAdapter = true)
data class ContentItem(
    @Json(name = "role") val role: String? = null,
    @Json(name = "parts") val parts: List<PartItem>
)

@JsonClass(generateAdapter = true)
data class PartItem(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineDataItem? = null
)

@JsonClass(generateAdapter = true)
data class InlineDataItem(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfigItem(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "topK") val topK: Int? = null,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null,
    @Json(name = "responseModalities") val responseModalities: List<String>? = null,
    @Json(name = "imageConfig") val imageConfig: ImageConfigItem? = null
)

@JsonClass(generateAdapter = true)
data class ImageConfigItem(
    @Json(name = "aspectRatio") val aspectRatio: String = "1:1",
    @Json(name = "imageSize") val imageSize: String = "1K"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<CandidateItem>? = null,
    @Json(name = "usageMetadata") val usageMetadata: UsageMetadataItem? = null,
    @Json(name = "error") val error: ApiErrorItem? = null
)

@JsonClass(generateAdapter = true)
data class CandidateItem(
    @Json(name = "content") val content: ContentItem? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class UsageMetadataItem(
    @Json(name = "promptTokenCount") val promptTokenCount: Int? = null,
    @Json(name = "candidatesTokenCount") val candidatesTokenCount: Int? = null,
    @Json(name = "totalTokenCount") val totalTokenCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class ApiErrorItem(
    @Json(name = "code") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "status") val status: String? = null
)
