package com.tans.gameoflife

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() = runBlocking<Unit> {
        val channel = BroadcastChannel<Int>(Channel.CONFLATED)
        channel.send(1)
        println("Received1: ${channel.asFlow().first()}")
        println("Received2: ${channel.asFlow().first()}")
    }
}
