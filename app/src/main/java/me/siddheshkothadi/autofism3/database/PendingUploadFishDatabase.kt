package me.siddheshkothadi.autofism3.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PendingUploadFishEntity::class], version = 1, exportSchema = false)
abstract class PendingUploadFishDatabase: RoomDatabase() {
    abstract fun fishDAO(): PendingUploadFishDAO
}