package com.wedd0031.flinders.zootreasurehunt.data

import android.content.Context
import com.wedd0031.flinders.zootreasurehunt.R
import com.wedd0031.flinders.zootreasurehunt.model.Sighting
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RoomSightingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sightingDao: SightingDao
) : SightingRepository {

    override suspend fun loadSightings(): List<Sighting> {
        val savedSightings = sightingDao.getAllSightings()

        if (savedSightings.isEmpty()) {
            val defaults = getDefaultSightings()
            sightingDao.insertSightings(defaults.map { it.toEntity() })
            return defaults
        }

        return localiseSightings(savedSightings.map { it.toSighting() })
    }

    override suspend fun saveSightings(sightings: List<Sighting>) {
        sightingDao.insertSightings(sightings.map { it.toEntity() })
    }

    override suspend fun addSighting(sighting: Sighting) {
        sightingDao.insertSightings(listOf(sighting.toEntity()))
    }

    override suspend fun updateSighting(sighting: Sighting) {
        sightingDao.updateSighting(sighting.toEntity())
    }

    override suspend fun deleteSighting(sighting: Sighting) {
        sightingDao.deleteSighting(sighting.toEntity())
    }

    private fun getAnimalName(animalKey: String): String {
        return when (animalKey) {
            "lion" -> context.getString(R.string.lion_name)
            "red_panda" -> context.getString(R.string.redpanda_name)
            "giraffe" -> context.getString(R.string.giraffe_name)
            "kangaroo" -> context.getString(R.string.kangaroo_name)
            "penguin" -> context.getString(R.string.penguin_name)
            else -> animalKey
        }
    }

    private fun localiseSightings(sightings: List<Sighting>): List<Sighting> {
        return sightings.map { sighting ->
            if (sighting.animalKey.isNotEmpty()) {
                sighting.copy(
                    name = getAnimalName(sighting.animalKey),
                    imageUrl = sighting.imageUrl.ifBlank {
                        getAnimalImageUrl(sighting.animalKey)
                    }
                )
            } else {
                sighting
            }
        }
    }

    private fun getAnimalImageUrl(animalKey: String): String {
        return when (animalKey) {
            "lion" -> "https://wilk0077.github.io/comp2012-images/assets-sm/african-lion-ai.jpg"
            "red_panda" -> "https://wilk0077.github.io/comp2012-images/assets-sm/red-panda-ai.jpg"
            "giraffe" -> "https://wilk0077.github.io/comp2012-images/assets-sm/giraffe-ai.jpg"
            "kangaroo" -> "https://wilk0077.github.io/comp2012-images/assets-sm/red-kangaroo-ai.jpg"
            "penguin" -> "https://wilk0077.github.io/comp2012-images/assets-sm/penguin-ai.jpg"
            else -> ""
        }
    }

    private fun getDefaultSightings(): List<Sighting> {
        return listOf(
            Sighting(
                name = context.getString(R.string.lion_name),
                animalKey = "lion",
                imageUrl = getAnimalImageUrl("lion")
            ),
            Sighting(
                name = context.getString(R.string.redpanda_name),
                animalKey = "red_panda",
                imageUrl = getAnimalImageUrl("red_panda")
            ),
            Sighting(
                name = context.getString(R.string.giraffe_name),
                animalKey = "giraffe",
                imageUrl = getAnimalImageUrl("giraffe")
            ),
            Sighting(
                name = context.getString(R.string.kangaroo_name),
                animalKey = "kangaroo",
                imageUrl = getAnimalImageUrl("kangaroo")
            ),
            Sighting(
                name = context.getString(R.string.penguin_name),
                animalKey = "penguin",
                imageUrl = getAnimalImageUrl("penguin")
            )
        )
    }
}
