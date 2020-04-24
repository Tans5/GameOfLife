package com.tans.gameoflife

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.firstOrNull
import kotlinx.coroutines.channels.produce
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
        val channel: Channel<Boolean> = Channel(Channel.CONFLATED)

        channel.send(false)

        launch {
            channel.receiveAsFlow()
                .collect {
                    println("receive1: $it")
                }
        }

        launch {
            channel.receiveAsFlow()
                .collect {
                    println("receive2: $it")
                }
        }
    }
}
