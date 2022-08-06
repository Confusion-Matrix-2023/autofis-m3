package me.siddheshkothadi.autofism3.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingUploadFishDAO {
    @Query("SELECT * FROM pendinguploadfishentity")
    fun getAll(): Flow<List<PendingUploadFishEntity>>

    @Query("SELECT * FROM pendinguploadfishentity")
    suspend fun getFishList(): List<PendingUploadFishEntity>

    @Query("SELECT * FROM pendinguploadfishentity WHERE imageUri = :imageUri")
    suspend fun getByImageUri(imageUri: String): PendingUploadFishEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingUploadFishEntity: PendingUploadFishEntity)

    @Delete
    suspend fun delete(pendingUploadFishEntity: PendingUploadFishEntity)
}