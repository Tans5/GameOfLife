package com.tans.gameoflife

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> (suspend (T) -> Unit).executeInCoroutine(scope: CoroutineScope, t: T) {
    scope.launch { this@executeInCoroutine.invoke(t) }
}

fun <T> Flow<T>.collectInCoroutine(scope: CoroutineScope, collect: suspend (T) -> Unit) {
    scope.launch { this@collectInCoroutine.collect(collect) }
}