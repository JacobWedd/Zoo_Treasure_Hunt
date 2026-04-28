package com.wedd0031.flinders.zootreasurehunt.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedd0031.flinders.zootreasurehunt.R
import com.wedd0031.flinders.zootreasurehunt.model.Sighting

@Composable
fun StatisticsScreen(
    sightings: List<Sighting>,
    onMenuClick: () -> Unit
) {
    val totalAnimals = sightings.size
    val foundAnimals = sightings.count { it.isFound }
    val remainingAnimals = totalAnimals - foundAnimals

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick) {
                Text(stringResource(R.string.menu_btn))
            }
            Text(
                text = stringResource(R.string.statistics_btn),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Text(text = stringResource(R.string.total_animals_label) + ": " + totalAnimals)
        Text(text = stringResource(R.string.found_animals_label) + ": " + foundAnimals)
        Text(text = stringResource(R.string.remaining_animals_label) + ": " + remainingAnimals)
    }
}