package com.wedd0031.flinders.zootreasurehunt.utils

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

class MicrophoneVolumeMonitor {
    companion object {
        const val LOUD_THRESHOLD = 1500
    }

    @SuppressLint("MissingPermission")
    fun volumeLevels(): Flow<Int> = flow {
        val sampleRate = 8000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        if (bufferSize <= 0) {
            throw IllegalStateException("Microphone is not available")
        }

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            throw IllegalStateException("Microphone is not available")
        }

        val buffer = ShortArray(bufferSize / 2)

        try {
            recorder.startRecording()

            while (currentCoroutineContext().isActive) {
                val samplesRead = recorder.read(buffer, 0, buffer.size)

                if (samplesRead > 0) {
                    var totalVolume = 0L
                    for (i in 0 until samplesRead) {
                        totalVolume += kotlin.math.abs(buffer[i].toInt())
                    }

                    val averageVolume = (totalVolume / samplesRead).toInt()
                    emit(averageVolume)
                } else {
                    emit(0)
                }

                delay(READ_DELAY_MS)
            }
        } finally {
            if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop()
            }
            recorder.release()
        }
    }.flowOn(Dispatchers.IO)
}
private const val READ_DELAY_MS = 200L
