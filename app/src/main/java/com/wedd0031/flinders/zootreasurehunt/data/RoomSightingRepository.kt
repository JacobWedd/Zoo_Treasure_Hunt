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
            markPossumDefaultChecked()
            return defaults
        }

        return localiseSightings(addPossumIfNeeded(savedSightings.map { it.toSighting() }))
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
            "common_brushtail_possum" -> context.getString(R.string.common_brushtail_possum_name)
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
            "common_brushtail_possum" -> "https://images.unsplash.com/photo-1720188490664-bea933e5aa7c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wxMjA3fDB8MXxhbGx8fHx8fHx8fHwxNzc5Mjk0ODAxfA&ixlib=rb-4.1.0&q=80&w=400"
            else -> ""
        }
    }

    private suspend fun addPossumIfNeeded(savedSightings: List<Sighting>): List<Sighting> {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val possumKey = "common_brushtail_possum"
        val possumHasAlreadyBeenChecked = prefs.getBoolean("possum_default_checked", false)
        val listAlreadyHasPossum = savedSightings.any { it.animalKey == possumKey }

        if (listAlreadyHasPossum || possumHasAlreadyBeenChecked) {
            if (listAlreadyHasPossum && !possumHasAlreadyBeenChecked) {
                markPossumDefaultChecked()
            }
            return savedSightings
        }

        val possum = getDefaultSightings().first { it.animalKey == possumKey }
        sightingDao.insertSightings(listOf(possum.toEntity()))
        markPossumDefaultChecked()

        return savedSightings + possum
    }

    private fun markPossumDefaultChecked() {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("possum_default_checked", true)
            .apply()
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
            ),
            Sighting(
                name = context.getString(R.string.common_brushtail_possum_name),
                animalKey = "common_brushtail_possum",
                imageUrl = getAnimalImageUrl("common_brushtail_possum")
            )
        )
    }
}
