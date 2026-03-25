package com.wedd0031.flinders.zootreasurehunt

import java.util.UUID

data class Sighting(
    val name: String,
    val isFound: Boolean = false,
    val notes: String = "",
    val id: String = UUID.randomUUID().toString(),

)
