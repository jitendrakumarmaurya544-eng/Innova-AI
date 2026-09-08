package com.example.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Chat", // Chat, Write, Code, Image, Study, Translate, Summarize, Brainstorm
    val pinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val previewSnippet: String = "",
    val modelUsed: String = "gemini-3.5-flash"
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val role: String, // "user", "model", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageBase64: String? = null,
    val isSaved: Boolean = false,
    val reaction: String? = null
)

@Entity(tableName = "saved_creations")
data class SavedCreationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "image", "code", "write", "summary", "quiz", "translation"
    val content: String,
    val imageBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: String = ""
)
