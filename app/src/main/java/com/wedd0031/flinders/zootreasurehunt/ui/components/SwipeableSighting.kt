package com.wedd0031.flinders.zootreasurehunt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wedd0031.flinders.zootreasurehunt.ui.components.AnimalCard
import com.wedd0031.flinders.zootreasurehunt.model.Sighting
import kotlin.math.roundToInt

enum class DragAnchors {
    START,
    END,
}

@Composable
fun SwipeableSighting(
    sighting: Sighting,
    onEditClick: (Sighting) -> Unit,
    onSwipe: (Sighting) -> Unit,
) {
    val dragState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.START
        )
    }
    LaunchedEffect(dragState.settledValue) {
        if (dragState.settledValue == DragAnchors.END) {
            onSwipe(sighting)
        }
    }
    val cardShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .onSizeChanged { layoutSize ->
                val dragEndPoint = layoutSize.width.toFloat()
                dragState.updateAnchors(
                    DraggableAnchors {
                        DragAnchors.START at 0f
                        DragAnchors.END at dragEndPoint
                    }
                )
            }
    ) {
        // Background Layer (Red Delete)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Red),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "Delete",
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = dragState.offset.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = dragState,
                    orientation = Orientation.Horizontal
                ),
            shape = cardShape
        ) {
            AnimalCard(
                sighting = sighting,
                onClick = { onEditClick(sighting) }
            )
        }
    }
}
