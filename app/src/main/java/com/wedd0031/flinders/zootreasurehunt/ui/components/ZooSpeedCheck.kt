package com.wedd0031.flinders.zootreasurehunt.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.wedd0031.flinders.zootreasurehunt.R
import kotlin.math.abs
import kotlin.math.sqrt

private const val STRONG_MOVEMENT_THRESHOLD = 6f
private const val REQUIRED_STRONG_MOVEMENTS = 5
private const val MOVEMENT_GROUP_TIME_MS = 2000L
private const val ALERT_COOLDOWN_MS = 30000L

@Composable
fun ZooSpeedCheck() {
    val context = LocalContext.current
    var showPaceDialog by remember { mutableStateOf(false) }
    var paceReply by remember { mutableStateOf<String?>(null) }

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val paceSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    DisposableEffect(paceSensor) {
        if (paceSensor == null) {
            onDispose { }
        } else {
            var movementCount = 0
            var lastMovementTime = 0L
            var lastAlertTime = 0L
            var hasLastReading = false
            var lastX = 0f
            var lastY = 0f
            var lastZ = 0f

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val now = System.currentTimeMillis()
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val movementAmount = if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                        sqrt(x * x + y * y + z * z)
                    } else if (hasLastReading) {
                        abs(x - lastX) + abs(y - lastY) + abs(z - lastZ)
                    } else {
                        0f
                    }

                    hasLastReading = true
                    lastX = x
                    lastY = y
                    lastZ = z

                    if (movementAmount > STRONG_MOVEMENT_THRESHOLD) {
                        if (now - lastMovementTime > MOVEMENT_GROUP_TIME_MS) {
                            movementCount = 0
                        }

                        movementCount++
                        lastMovementTime = now
                    }

                    if (
                        movementCount >= REQUIRED_STRONG_MOVEMENTS &&
                        now - lastAlertTime > ALERT_COOLDOWN_MS &&
                        !showPaceDialog
                    ) {
                        showPaceDialog = true
                        paceReply = null
                        movementCount = 0
                        lastAlertTime = now
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // No accuracy handling needed for this simple pace check.
                }
            }

            sensorManager.registerListener(
                listener,
                paceSensor,
                SensorManager.SENSOR_DELAY_GAME
            )

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    if (showPaceDialog) {
        AlertDialog(
            onDismissRequest = { showPaceDialog = false },
            title = { Text(stringResource(R.string.zoo_speed_check_title)) },
            text = {
                Text(paceReply ?: stringResource(R.string.zoo_speed_check_question))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (paceReply == null) {
                            paceReply = context.getString(R.string.zoo_speed_check_yes_reply)
                        } else {
                            showPaceDialog = false
                        }
                    }
                ) {
                    Text(
                        if (paceReply == null) {
                            stringResource(R.string.yes_btn)
                        } else {
                            stringResource(R.string.ok_btn)
                        }
                    )
                }
            },
            dismissButton = {
                if (paceReply == null) {
                    TextButton(
                        onClick = {
                            paceReply = context.getString(R.string.zoo_speed_check_no_reply)
                        }
                    ) {
                        Text(stringResource(R.string.no_btn))
                    }
                }
            }
        )
    }
}
