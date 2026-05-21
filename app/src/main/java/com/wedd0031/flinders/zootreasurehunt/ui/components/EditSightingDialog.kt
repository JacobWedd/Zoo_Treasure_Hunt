package com.wedd0031.flinders.zootreasurehunt.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.wedd0031.flinders.zootreasurehunt.R
import com.wedd0031.flinders.zootreasurehunt.model.Sighting
import com.wedd0031.flinders.zootreasurehunt.utils.FileUtils
import com.wedd0031.flinders.zootreasurehunt.utils.MicrophoneVolumeMonitor
import kotlinx.coroutines.flow.catch

@Composable
fun EditSightingDialog(sighting: Sighting, onDismiss: () -> Unit, onSave: (Sighting) -> Unit) {
    val context = LocalContext.current
    var notesText by remember(sighting.id) { mutableStateOf(sighting.notes) }
    var isFoundChecked by remember(sighting.id) { mutableStateOf(sighting.isFound) }
    var verificationMessage by remember(sighting.id) { mutableStateOf<String?>(null) }
    var microphoneMessage by remember(sighting.id) { mutableStateOf<String?>(null) }
    var currentVolume by remember(sighting.id) { mutableStateOf(0) }
    var hasMicrophonePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isTooLoud by remember(sighting.id) { mutableStateOf(false) }
    var loudReadingsInARow by remember(sighting.id) { mutableStateOf(0) }

    val fileUtils = remember { FileUtils(context) }
    val microphoneMonitor = remember(sighting.id) { MicrophoneVolumeMonitor() }
    var currentPhotoPath by remember(sighting.id) { mutableStateOf(sighting.photoPath) }
    var tempPhotoUri by remember(sighting.id) { mutableStateOf<Uri?>(null) }
    val isPossum = sighting.animalKey == "common_brushtail_possum" ||
            sighting.name.contains("possum", ignoreCase = true)
    val isNocturnalAnimal = isPossum
    var isNocturnalModeActive by remember(sighting.id) { mutableStateOf(false) }
    val isShyAnimal = sighting.animalKey == "red_panda" ||
            sighting.animalKey == "penguin" ||
            isPossum ||
            sighting.name.contains("red panda", ignoreCase = true) ||
            sighting.name.contains("penguin", ignoreCase = true)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            currentPhotoPath = tempPhotoUri.toString()
            isFoundChecked = true
            verificationMessage = context.getString(R.string.photo_verified_label)
        } else {
            verificationMessage = context.getString(R.string.photo_failed_label)
        }
    }

    val launchCameraCapture = {
        val file = fileUtils.createImageFile()
        val uri = fileUtils.getUriForFile(file)
        tempPhotoUri = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCameraCapture()
        } else {
            verificationMessage = context.getString(R.string.camera_permission_denied_label)
        }
    }

    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicrophonePermission = isGranted
        microphoneMessage = if (isGranted) {
            context.getString(R.string.quiet_monitor_active_label)
        } else {
            context.getString(R.string.microphone_permission_denied_label)
        }
    }

    LaunchedEffect(sighting.id, isShyAnimal) {
        currentVolume = 0
        isTooLoud = false
        loudReadingsInARow = 0
        microphoneMessage = null

        if (isShyAnimal && !hasMicrophonePermission) {
            microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(sighting.id, isShyAnimal, hasMicrophonePermission) {
        if (isShyAnimal && hasMicrophonePermission) {
            microphoneMonitor.volumeLevels()
                .catch {
                    isTooLoud = false
                    loudReadingsInARow = 0
                    microphoneMessage = context.getString(R.string.microphone_unavailable_label)
                }
                .collect { volume ->
                    currentVolume = volume
                    loudReadingsInARow = if (volume > MicrophoneVolumeMonitor.LOUD_THRESHOLD) {
                        loudReadingsInARow + 1
                    } else {
                        0
                    }
                    isTooLoud = loudReadingsInARow >= 3
                    microphoneMessage = if (isTooLoud) {
                        context.getString(R.string.shy_animal_loud_warning)
                    } else {
                        context.getString(R.string.quiet_monitor_active_label)
                    }
                }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isNocturnalModeActive) {
            Color(0xFF111111)
        } else {
            AlertDialogDefaults.containerColor
        },
        titleContentColor = if (isNocturnalModeActive) {
            Color.White
        } else {
            AlertDialogDefaults.titleContentColor
        },
        textContentColor = if (isNocturnalModeActive) {
            Color.White
        } else {
            AlertDialogDefaults.textContentColor
        },
        title = { Text(text = stringResource(id = R.string.edit_animal)) },
        text = {
            Column {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(stringResource(id = R.string.notes_hint)) },
                    colors = if (isNocturnalModeActive) {
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = Color.White
                        )
                    } else {
                        OutlinedTextFieldDefaults.colors()
                    }
                )
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFoundChecked,
                        onCheckedChange = { checked ->
                            if (checked && currentPhotoPath == null) {
                                verificationMessage = context.getString(R.string.photo_required_label)
                            } else {
                                isFoundChecked = checked
                                verificationMessage = null
                            }
                        }
                    )
                    Text(text = stringResource(id = R.string.checkbox_found))
                }
                Button(
                    onClick = {
                        val hasCameraPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasCameraPermission) {
                            launchCameraCapture()
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = if (currentPhotoPath == null) stringResource(R.string.photo_btn) else stringResource(R.string.retake_photo_btn)
                    )
                }
                if (currentPhotoPath != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = currentPhotoPath,
                        contentDescription = stringResource(R.string.photo_preview_description),
                        modifier = Modifier.size(96.dp)
                    )
                    Text(
                        text = stringResource(R.string.attached_label),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                verificationMessage?.let { message ->
                    Text(
                        text = message,
                        color = if (currentPhotoPath == null) Color.Red else Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (isNocturnalAnimal) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NocturnalMode(
                        onNocturnalModeChanged = { isActive ->
                            isNocturnalModeActive = isActive
                        }
                    )
                }
                if (isShyAnimal) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.shy_animal_label),
                        color = if (isNocturnalModeActive) Color.White else Color(0xFF5D4037)
                    )
                    if (hasMicrophonePermission) {
                        Text(
                            text = stringResource(
                                if (isTooLoud) {
                                    R.string.sound_level_loud_label
                                } else {
                                    R.string.sound_level_quiet_label
                                }
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    microphoneMessage?.let { message ->
                        Text(
                            text = message,
                            color = if (isTooLoud) Color.Red else Color(0xFF2E7D32),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isFoundChecked && currentPhotoPath == null) {
                    verificationMessage = context.getString(R.string.photo_required_label)
                } else {
                    onSave(
                        sighting.copy(
                            isFound = isFoundChecked,
                            notes = notesText,
                            photoPath = currentPhotoPath
                        )
                    )
                }
            }) {
                Text(text = stringResource(id = R.string.save_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel_btn))
            }
        }
    )
}