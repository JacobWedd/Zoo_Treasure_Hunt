package com.wedd0031.flinders.zootreasurehunt

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Sighting(
    val name: String,
    val isFound: Boolean = false,
    val notes: String = "",
    val id: String = UUID.randomUUID().toString(),
    val imageUrl: String = "https://wilk0077.github.io/comp2012-images/assets-sm/african-lion-ai.jpg"
)
