package com.cssaimentor.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cssaimentor.app.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getFavorites(): List<FavoriteEntity>

    @Query("SELECT * FROM favorites WHERE itemId = :itemId LIMIT 1")
    suspend fun getFavorite(itemId: String): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Delete
    suspend fun delete(entity: FavoriteEntity)
}

