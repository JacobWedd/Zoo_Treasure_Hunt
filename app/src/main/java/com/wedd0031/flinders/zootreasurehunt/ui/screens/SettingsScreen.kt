package com.wedd0031.flinders.zootreasurehunt.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isSortByName: Boolean,
    onSortChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Sort Order")

        Row(modifier = Modifier.padding(top = 8.dp)) {
            RadioButton(
                selected = isSortByName,
                onClick = { onSortChange(true) }
            )
            Text(text = "Sort by Name", modifier = Modifier.padding(start = 8.dp))
        }

        Row(modifier = Modifier.padding(top = 8.dp)) {
            RadioButton(
                selected = !isSortByName,
                onClick = { onSortChange(false) }
            )
            Text(text = "Sort by Date", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

