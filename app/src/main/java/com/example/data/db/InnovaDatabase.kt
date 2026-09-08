package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        SavedCreationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class InnovaDatabase : RoomDatabase() {

    abstract fun innovaDao(): InnovaDao

    companion object {
        @Volatile
        private var INSTANCE: InnovaDatabase? = null

        fun getDatabase(context: Context): InnovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InnovaDatabase::class.java,
                    "innova_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
