package com.wedd0031.flinders.zootreasurehunt.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SightingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ZooDatabase : RoomDatabase() {
    abstract fun sightingDao(): SightingDao
}
