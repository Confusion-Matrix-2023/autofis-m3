package me.siddheshkothadi.autofism3.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FishEntity::class], version = 1, exportSchema = false)
abstract class FishDatabase: RoomDatabase() {
    abstract fun fishDAO(): FishDAO
}