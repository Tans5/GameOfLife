package com.tans.gameoflife

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.core.view.ViewCompat
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

suspend fun View.postInvalidateOnAnimation(delay: Long): Unit = suspendCoroutine { cont ->
    ViewCompat.postOnAnimationDelayed(this, { ViewCompat.postInvalidateOnAnimation(this); cont.resume(Unit) }, delay)
}

fun Context.getScreenSize(): Pair<Int, Int> {
    val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x to size.y
}