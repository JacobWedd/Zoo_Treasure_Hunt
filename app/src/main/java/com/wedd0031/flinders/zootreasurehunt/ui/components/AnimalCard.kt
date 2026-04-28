package com.wedd0031.flinders.zootreasurehunt.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wedd0031.flinders.zootreasurehunt.R
import com.wedd0031.flinders.zootreasurehunt.model.Sighting
import java.text.DateFormat
import java.util.Date

@Composable
fun AnimalCard(sighting: Sighting, onClick: () -> Unit) {

    val cardColor by animateColorAsState(
        targetValue = if (sighting.isFound) Color(0xFFC8E6C9) else Color(0xFFF5F5F5),
        animationSpec = tween(750),
        label = "cardColor"
    )

    val textColor = if (sighting.isFound) Color(0xFF2E7D32) else Color.Black
    val imageModel = sighting.photoPath ?: sighting.imageUrl


    val cardElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (sighting.isFound) 12.dp else 2.dp,
        label = "cardElevation"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (sighting.isFound) 1.05f else 1f,
        label = "cardScale"
    )
    val foundScale by animateFloatAsState(
        targetValue = if (sighting.isFound) 1.2f else 1f,
        animationSpec = tween(500),
        label = "foundScale"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = sighting.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sighting.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (sighting.isFound && sighting.notes.isNotEmpty()) {
                    Text(
                        text = sighting.notes,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                AnimatedVisibility(
                    visible = sighting.isFound && sighting.timestamp > 0L,
                    enter = fadeIn(tween (750) )
                ) {
                    Text(
                        text = DateFormat.getDateTimeInstance().format(Date(sighting.timestamp)),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            if (sighting.isFound) {
                Text(
                    text = stringResource(R.string.found_label),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.scale(foundScale)
                )
            }
        }
    }
}