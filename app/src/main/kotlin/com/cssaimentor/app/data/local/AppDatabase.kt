package com.cssaimentor.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cssaimentor.app.data.local.dao.ChatDao
import com.cssaimentor.app.data.local.dao.FavoriteDao
import com.cssaimentor.app.data.local.dao.PdfBookmarkDao
import com.cssaimentor.app.data.local.entity.ChatEntity
import com.cssaimentor.app.data.local.entity.FavoriteEntity
import com.cssaimentor.app.data.local.entity.PdfBookmarkEntity

@Database(
    entities = [FavoriteEntity::class, ChatEntity::class, PdfBookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun chatDao(): ChatDao
    abstract fun pdfBookmarkDao(): PdfBookmarkDao
}
