package com.wedd0031.flinders.zootreasurehunt.data

import android.content.Context
import com.wedd0031.flinders.zootreasurehunt.R
import com.wedd0031.flinders.zootreasurehunt.model.Sighting
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class FileSightingRepository @Inject constructor (
    @ApplicationContext private val context: Context
) : SightingRepository {

    private val fileName = "sightings.json"

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
                    name = getAnimalName(sighting.animalKey)
                )
            } else {
                sighting
            }
        }
    }

    override suspend fun saveSightings(sightings: List<Sighting>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = Json.encodeToString(sightings)

                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getDefaultSightings(): List<Sighting> {
        return listOf(
            Sighting(
                name = context.getString(R.string.lion_name),
                animalKey = "lion",
                imageUrl = "https://wilk0077.github.io/comp2012-images/assets-sm/african-lion-ai.jpg"
            ),
            Sighting(
                name = context.getString(R.string.redpanda_name),
                animalKey = "red_panda",
                imageUrl = "https://wilk0077.github.io/comp2012-images/assets-sm/red-panda-ai.jpg"
            ),
            Sighting(
                name = context.getString(R.string.giraffe_name),
                animalKey = "giraffe",
                imageUrl = "https://wilk0077.github.io/comp2012-images/assets-sm/giraffe-ai.jpg"
            ),
            Sighting(
                name = context.getString(R.string.kangaroo_name),
                animalKey = "kangaroo",
                imageUrl = "https://wilk0077.github.io/comp2012-images/assets-sm/red-kangaroo-ai.jpg"
            ),
            Sighting(
                name = context.getString(R.string.penguin_name),
                animalKey = "penguin",
                imageUrl = "https://wilk0077.github.io/comp2012-images/assets-sm/penguin-ai.jpg"
            )
        )
    }

    override suspend fun loadSightings(): List<Sighting> {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withContext getDefaultSightings()

            try {
                val jsonString = context.openFileInput(fileName).bufferedReader().use {
                    it.readText()
                }
                val savedSightings: List<Sighting> = Json.decodeFromString(jsonString)
                localiseSightings(savedSightings)
            } catch (e: Exception) {
                getDefaultSightings()
            }
        }
    }

    override suspend fun addSighting(sighting: Sighting) {
        val currentList = loadSightings().toMutableList()
        currentList.add(sighting)
        saveSightings(currentList)
    }

    override suspend fun updateSighting(sighting: Sighting) {
        val currentList = loadSightings().map {
            if (it.id == sighting.id) sighting else it
        }
        saveSightings(currentList)
    }

    override suspend fun deleteSighting(sighting: Sighting) {
        val currentList = loadSightings().filter { it.id != sighting.id }
        saveSightings(currentList)
    }
}