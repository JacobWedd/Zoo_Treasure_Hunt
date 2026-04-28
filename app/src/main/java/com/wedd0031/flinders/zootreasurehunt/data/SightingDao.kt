package com.wedd0031.flinders.zootreasurehunt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SightingDao {

    @Query("SELECT * FROM sightings")
    suspend fun getAllSightings(): List<SightingEntity>

    @Query("SELECT * FROM sightings ORDER BY name ASC")
    suspend fun getSightingsSortedByName(): List<SightingEntity>

    @Query("SELECT * FROM sightings ORDER BY isFound ASC")
    suspend fun getSightingsSortedByFound(): List<SightingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSightings(sightings: List<SightingEntity>)

    @Update
    suspend fun updateSighting(sighting: SightingEntity)

    @Delete
    suspend fun deleteSighting(sighting: SightingEntity)

    @Query("DELETE FROM sightings")
    suspend fun deleteAllSightings()
}
