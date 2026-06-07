package com.cssaimentor.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cssaimentor.app.data.local.entity.PdfBookmarkEntity

@Dao
interface PdfBookmarkDao {
    @Query("SELECT pageIndex FROM pdf_bookmarks WHERE documentId = :documentId LIMIT 1")
    suspend fun getBookmark(documentId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PdfBookmarkEntity)
}

