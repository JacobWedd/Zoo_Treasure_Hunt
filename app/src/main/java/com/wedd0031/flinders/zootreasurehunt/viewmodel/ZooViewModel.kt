package com.wedd0031.flinders.zootreasurehunt.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.WorkManager
import com.wedd0031.flinders.zootreasurehunt.model.Sighting
import com.wedd0031.flinders.zootreasurehunt.data.SightingRepository
import com.wedd0031.flinders.zootreasurehunt.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.wedd0031.flinders.zootreasurehunt.worker.CongratulationWorker
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import com.wedd0031.flinders.zootreasurehunt.model.ZooUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ZooViewModel @Inject constructor(
    application: Application,
    private val repository: SightingRepository,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    private val _sightings = MutableStateFlow<List<Sighting>>(emptyList())
    private val _uiState = MutableStateFlow(ZooUiState())
    private val _rawSightings = MutableStateFlow<List<Sighting>>(emptyList())
    val sightings: StateFlow<List<Sighting>> = _sightings.asStateFlow()
    val uiState: StateFlow<ZooUiState> = _uiState.asStateFlow()
    val isSortByName = settingsRepository.sortByNameFlow
    val isDarkMode = settingsRepository.darkModeFlow

    init {
        viewModelScope.launch {
            _rawSightings.value = repository.loadSightings()
        }

        viewModelScope.launch {
            combine(_rawSightings, settingsRepository.sortByNameFlow) { list, sortByName ->
                val sortedList = if (sortByName) {
                    list.sortedBy { it.name }
                } else {
                    list.sortedBy { it.isFound } //Found animals set to bottom of list+ for easier view of remaining animals to find.
                }
                _uiState.value.copy(sightings = sortedList, isSortByName = sortByName)
            }.collect { newState ->
                _uiState.value = newState
                _sightings.value = newState.sightings
            }
        }
    }

    fun updateSighting(updated: Sighting) {
        val oldSighting = _rawSightings.value.find { it.id == updated.id }

        val updatedWithTimestamp = if (updated.isFound && oldSighting?.isFound == false) {
            val workRequest = OneTimeWorkRequestBuilder<CongratulationWorker>()
                .setInputData(workDataOf("ANIMAL_NAME" to updated.name))
                .build()

            workManager.enqueue(workRequest)
            updated.copy(timestamp = System.currentTimeMillis())
        } else {
            updated.copy(timestamp = oldSighting?.timestamp ?: updated.timestamp)
        }

        val newList = _rawSightings.value.map {
            if (it.id == updated.id) updatedWithTimestamp else it
        }

        _rawSightings.value = newList
        viewModelScope.launch {
            repository.updateSighting(updatedWithTimestamp)
        }
    }

    fun deleteSighting(sighting: Sighting) {
        val newList = _rawSightings.value.filter { it.id != sighting.id }
        _rawSightings.value = newList
        viewModelScope.launch {
            repository.deleteSighting(sighting)
        }
    }
    fun toggleSortOrder(sortByName: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSortByName(sortByName)
        }
    }

    fun toggleDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(isDarkMode)
        }
    }

    fun selectSightingForEdit(sighting: Sighting?) {
        _uiState.value = _uiState.value.copy(
            selectedSighting = sighting,
            isDialogVisible = sighting != null
        )
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(
            selectedSighting = null,
            isDialogVisible = false
        )
    }
}
