package me.siddheshkothadi.autofism3.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.model.Fish

@Dao
interface FishDAO {
    @Query("SELECT * FROM fishentity")
    fun getAll(): Flow<List<FishEntity>>

    @Query("SELECT * FROM fishentity")
    suspend fun getFishList(): List<FishEntity>

    @Query("SELECT * FROM fishentity WHERE imageUri = :imageUri")
    suspend fun getByImageUri(imageUri: String): FishEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fishEntity: FishEntity)

    @Delete
    suspend fun delete(fishEntity: FishEntity)

    @Query("DELETE FROM fishentity")
    suspend fun deleteAll()
}