package com.cssaimentor.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val itemId: String,
    val type: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey val id: String,
    val role: String,
    val text: String,
    val createdAt: Long
)

@Entity(tableName = "pdf_bookmarks")
data class PdfBookmarkEntity(
    @PrimaryKey val documentId: String,
    val pageIndex: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

