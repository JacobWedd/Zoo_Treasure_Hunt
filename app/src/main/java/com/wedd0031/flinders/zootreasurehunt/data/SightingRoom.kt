package com.wedd0031.flinders.zootreasurehunt.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wedd0031.flinders.zootreasurehunt.model.Sighting

@Entity(tableName = "sightings")
data class SightingEntity(
    @PrimaryKey val id: String,
    val name: String,
    val animalKey: String,
    val isFound: Boolean,
    val notes: String,
    val imageUrl: String,
    val photoPath: String?,
    val timestamp: Long
)

fun SightingEntity.toSighting(): Sighting {
    return Sighting(
        id = id,
        name = name,
        animalKey = animalKey,
        isFound = isFound,
        notes = notes,
        imageUrl = imageUrl,
        photoPath = photoPath,
        timestamp = timestamp
    )
}

fun Sighting.toEntity(): SightingEntity {
    return SightingEntity(
        id = id,
        name = name,
        animalKey = animalKey,
        isFound = isFound,
        notes = notes,
        imageUrl = imageUrl,
        photoPath = photoPath,
        timestamp = timestamp
    )
}
