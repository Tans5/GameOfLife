package com.tans.gameoflife

import android.view.View
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