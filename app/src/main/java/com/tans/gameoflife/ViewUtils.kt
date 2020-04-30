package com.tans.gameoflife

import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun View.clicks(): Flow<Unit> = flow {
    while (true) {
        clickCallToSuspend()
        emit(Unit)
    }
}

suspend fun View.clickCallToSuspend(): Unit {
    return suspendCoroutine<Unit> { continuation ->
        setOnClickListener {
            continuation.resume(Unit)
            this.setOnClickListener(null)
        }
    }
}

fun SeekBar.progressChange(): Flow<Int> = flow {
    while (true) {
        emit(progressChangeCallToSuspend())
    }
}

suspend fun SeekBar.progressChangeCallToSuspend(): Int {
    return suspendCoroutine<Int> { cont ->
        this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cont.resume(progress)
                this@progressChangeCallToSuspend.setOnSeekBarChangeListener(null)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}

fun CompoundButton.checkChanges(): Flow<Boolean> = flow {
    while (true) { emit(checkChangeCallToSuspend()) }
}

suspend fun CompoundButton.checkChangeCallToSuspend(): Boolean {
    return suspendCoroutine { cont ->
        this.setOnCheckedChangeListener { _, isChecked ->
            cont.resume(isChecked)
            this.setOnCheckedChangeListener(null)
        }
    }
}