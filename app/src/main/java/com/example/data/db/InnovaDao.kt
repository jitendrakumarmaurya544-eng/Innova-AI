package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InnovaDao {

    // Conversations
    @Query("SELECT * FROM conversations ORDER BY pinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE category = :category ORDER BY pinned DESC, updatedAt DESC")
    fun getConversationsByCategory(category: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET pinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean)

    @Query("UPDATE conversations SET updatedAt = :timestamp, previewSnippet = :snippet WHERE id = :id")
    suspend fun updateLastActivity(id: Long, timestamp: Long, snippet: String)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("DELETE FROM conversations")
    suspend fun clearAllConversations()

    // Messages
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesListForConversation(conversationId: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("UPDATE messages SET isSaved = :isSaved WHERE id = :id")
    suspend fun setMessageSaved(id: Long, isSaved: Boolean)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    // Saved creations / bookmarks
    @Query("SELECT * FROM saved_creations ORDER BY timestamp DESC")
    fun getAllSavedCreations(): Flow<List<SavedCreationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCreation(creation: SavedCreationEntity): Long

    @Delete
    suspend fun deleteSavedCreation(creation: SavedCreationEntity)

    @Query("DELETE FROM saved_creations WHERE id = :id")
    suspend fun deleteSavedCreationById(id: Long)
}
