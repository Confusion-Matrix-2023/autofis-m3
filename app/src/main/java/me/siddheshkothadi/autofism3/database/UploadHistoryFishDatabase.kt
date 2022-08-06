package me.siddheshkothadi.autofism3.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UploadHistoryFishEntity::class], version = 1, exportSchema = false)
abstract class UploadHistoryFishDatabase: RoomDatabase() {
    abstract fun fishDAO(): UploadHistoryFishDAO
}