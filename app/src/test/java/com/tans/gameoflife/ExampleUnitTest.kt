package com.tans.gameoflife

import com.tans.gameoflife.game.DEFAULT_GOLLY_CODE
import com.tans.gameoflife.game.GollyCodeParser
import com.tans.gameoflife.game.margin
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() = runBlocking<Unit> {
//        val channel = BroadcastChannel<Int>(Channel.CONFLATED)
//        channel.send(1)
//        println("Received1: ${channel.asFlow().first()}")
//        println("Received2: ${channel.asFlow().first()}")
//        val result = GollyCodeParser.parseToSquareData(11, 11, "2b4o\$bo4bob2o\$bo4bo3bo\$4b2o4bo\$b2o4bo2bo\$o2bo3bo2bo\$o2bo4b2o\$o4b2o\$o3bo4bo\$b2obo4bo!")
//        val isAlive = result.isAlive(1, 1)
//        println(result)
        val result = GollyCodeParser(DEFAULT_GOLLY_CODE[0])
        println(result)
    }
}
