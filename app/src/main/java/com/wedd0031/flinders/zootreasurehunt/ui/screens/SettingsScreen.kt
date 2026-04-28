package com.wedd0031.flinders.zootreasurehunt.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedd0031.flinders.zootreasurehunt.R
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment

@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit,
    isSortByName: Boolean,
    onSortChange: (Boolean) -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
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
                text = stringResource(R.string.settings_btn),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(text = stringResource(R.string.sort_label))

        Row(modifier = Modifier.padding(top = 8.dp)) {
            RadioButton(
                selected = isSortByName,
                onClick = { onSortChange(true) }
            )
            Text(text = stringResource(R.string.sortbyname_btn), modifier = Modifier.padding(start = 8.dp))
        }

        Row(modifier = Modifier.padding(top = 8.dp)) {
            RadioButton(
                selected = !isSortByName,
                onClick = { onSortChange(false) }
            )
            Text(text = stringResource(R.string.sortbyfound_btn), modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dark_mode_label),
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = isDarkMode,
                onCheckedChange = { onDarkModeChange(it) }
            )
        }
    }
}
