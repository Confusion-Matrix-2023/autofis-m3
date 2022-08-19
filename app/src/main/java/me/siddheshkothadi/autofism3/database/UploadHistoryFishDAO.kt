package me.siddheshkothadi.autofism3.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadHistoryFishDAO {
    @Query("SELECT * FROM uploadhistoryfishentity")
    fun getAll(): Flow<List<UploadHistoryFishEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(uploadHistoryFishEntity: UploadHistoryFishEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(list: List<UploadHistoryFishEntity>)

    @Query("DELETE FROM uploadhistoryfishentity")
    fun deleteAll()
}