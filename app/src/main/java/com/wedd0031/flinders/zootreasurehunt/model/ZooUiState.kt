package com.wedd0031.flinders.zootreasurehunt.model

import com.wedd0031.flinders.zootreasurehunt.model.Sighting

data class ZooUiState(
    val sightings: List<Sighting> = emptyList(),
    val isSortByName: Boolean = true,
    val selectedSighting: Sighting? = null,
    val isDialogVisible: Boolean = false
)
