package com.wedd0031.flinders.zootreasurehunt.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wedd0031.flinders.zootreasurehunt.R

@Composable
fun NocturnalMode(
    modifier: Modifier = Modifier,
    onNocturnalModeChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val lightSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    var currentLux by remember { mutableStateOf<Float?>(null) }
    var hasLightSensor by remember { mutableStateOf(lightSensor != null) }
    val isDarkAroundPhone = currentLux != null && currentLux!! < DARK_ROOM_LUX

    LaunchedEffect(isDarkAroundPhone) {
        onNocturnalModeChanged(isDarkAroundPhone)
    }

    DisposableEffect(lightSensor) {
        if (lightSensor == null) {
            hasLightSensor = false
            return@DisposableEffect onDispose { }
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                currentLux = event.values.firstOrNull()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }

        sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val backgroundColor = if (isDarkAroundPhone) Color(0xFF111111) else Color(0xFFFFF8E1)
    val textColor = if (isDarkAroundPhone) Color.White else Color(0xFF4E342E)
    val borderColor = if (isDarkAroundPhone) Color.White else Color(0xFFFFB300)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.nocturnal_animal_message),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )

        val statusText = when {
            !hasLightSensor -> stringResource(R.string.light_sensor_unavailable_label)
            isDarkAroundPhone -> stringResource(R.string.nocturnal_mode_active_label)
            currentLux == null -> stringResource(R.string.light_sensor_waiting_label)
            else -> stringResource(R.string.nocturnal_mode_ready_label)
        }

        Text(
            text = statusText,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

private const val DARK_ROOM_LUX = 10f
