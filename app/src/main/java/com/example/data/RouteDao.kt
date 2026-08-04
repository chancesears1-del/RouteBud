package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM saved_routes ORDER BY timestamp DESC")
    fun getAllRoutes(): Flow<List<SavedRoute>>

    @Query("SELECT * FROM saved_routes WHERE username = :username ORDER BY timestamp DESC")
    fun getRoutesForUser(username: String): Flow<List<SavedRoute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: SavedRoute): Long

    @Query("DELETE FROM saved_routes WHERE id = :id")
    suspend fun deleteRoute(id: Int)

    @Query("DELETE FROM saved_routes")
    suspend fun clearAll()
}
