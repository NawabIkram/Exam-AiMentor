package com.cssaimentor.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cssaimentor.app.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY createdAt ASC")
    fun observeMessages(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY createdAt DESC LIMIT 1")
    suspend fun latestMessage(): ChatEntity?

    @Query("SELECT * FROM chat_messages WHERE role = 'User' ORDER BY createdAt DESC LIMIT 1")
    suspend fun latestUserMessage(): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clear()
}

