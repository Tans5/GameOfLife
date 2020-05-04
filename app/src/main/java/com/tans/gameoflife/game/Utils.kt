package com.tans.gameoflife.game

import androidx.annotation.IntRange
import kotlin.random.Random
import kotlin.random.nextInt

fun Size.getCoordinate(i: Int): Pair<Int, Int> {
    return if (i !in 0 until width * height) {
        error("$i is Out Bound Of with: $width, height: $height")
    } else {
        (i % width) to (i / width)
    }
}

fun Size.getArrayIndex(coordinate: Pair<Int, Int>): Int {
    return if (coordinate.first !in 0 until width || coordinate.second !in 0 until height) {
        error("x: ${coordinate.first}, y: ${coordinate.second} is not in Size: $this")
    } else {
        coordinate.second * width + coordinate.first % width
    }
}

fun Size.randomLife(@IntRange(from = 0L, to = 100L) probability: Int, seed: Long)
        : LifeModel {
    return LifeModel(
        life = sequence<Int> {
            repeat(width * height) { i ->
                val isAlive = probability > Random(seed - probability * i * 100).nextInt(1 .. 100)
                if (isAlive) {
                    yield(i)
                }
            }
        }.toList().toIntArray(),
        mapSize = this
    )
}

fun Size.randomLife2(@IntRange(from = 0L, to = 100L) probability: Int, seed: Long) : LifeModel2 {
    return LifeModel2(
        mapSize = this,
        life = MutableList(width * height) { index ->
            val isAlive = probability > Random(seed - probability * index * 100).nextInt(1 .. 100)
            val x = index % width
            val y = index / width
            Cell(x = x, y = y, isAlive = isAlive)
        }
    )
}

fun Size.getRoundIndex(me: Int): IntArray {
    return if (me !in 0 until width * height) {
        error("Index: $me out of Boundary: with: $width, height: $height ")
    } else {
        val (meX, meY) = getCoordinate(me)
        arrayOf<Pair<Int, Int>>(
            (meX - 1) to (meY - 1),
            meX to (meY - 1),
            (meX + 1) to (meY - 1),
            (meX + 1) to meY,
            (meX + 1) to (meY + 1),
            meX to (meY + 1),
            (meX - 1) to (meY + 1),
            (meX - 1) to meY
        ).filter { (x, y) -> x in 0 until width && y in 0 until height }
            .map { getArrayIndex(it) }
            .toIntArray()
    }
}

fun Size.getRoundAliveCount(me: Int, life: IntArray): Int = this.getRoundIndex(me).count { life.contains(it) }

const val INVALID_INDEX: Int = -1
const val ROUND_INDEX_RESULT_COUNT = 8
val roundIndexResult: MutableList<Int> = MutableList(ROUND_INDEX_RESULT_COUNT) { INVALID_INDEX }
fun LifeModel2.getRoundIndex(meIndex: Int): List<Int> {
    val width = mapSize.width
    val height = mapSize.height
    val mX = life[meIndex].x
    val mY = life[meIndex].y
    when {
        mX == 0 && mY == 0 -> {
            roundIndexResult[0] = INVALID_INDEX
            roundIndexResult[1] = INVALID_INDEX
            roundIndexResult[2] = INVALID_INDEX
            roundIndexResult[3] = meIndex + 1
            roundIndexResult[4] = meIndex + width + 1
            roundIndexResult[5] = meIndex + width
            roundIndexResult[6] = INVALID_INDEX
            roundIndexResult[7] = INVALID_INDEX
        }
        mX == (width - 1) && mY == 0 -> {
            roundIndexResult[0] = INVALID_INDEX
            roundIndexResult[1] = INVALID_INDEX
            roundIndexResult[2] = INVALID_INDEX
            roundIndexResult[3] = INVALID_INDEX
            roundIndexResult[4] = INVALID_INDEX
            roundIndexResult[5] = meIndex + width
            roundIndexResult[6] = meIndex + width - 1
            roundIndexResult[7] = meIndex - 1
        }
        mX == 0 && mY == height - 1 -> {
            roundIndexResult[0] = INVALID_INDEX
            roundIndexResult[1] = meIndex - width
            roundIndexResult[2] = meIndex - width + 1
            roundIndexResult[3] = meIndex + 1
            roundIndexResult[4] = INVALID_INDEX
            roundIndexResult[5] = INVALID_INDEX
            roundIndexResult[6] = INVALID_INDEX
            roundIndexResult[7] = INVALID_INDEX
        }
        mX == width - 1 && mY == height - 1 -> {
            roundIndexResult[0] = meIndex - width - 1
            roundIndexResult[1] = meIndex - width
            roundIndexResult[2] = INVALID_INDEX
            roundIndexResult[3] = INVALID_INDEX
            roundIndexResult[4] = INVALID_INDEX
            roundIndexResult[5] = INVALID_INDEX
            roundIndexResult[6] = INVALID_INDEX
            roundIndexResult[7] = meIndex - 1
        }
        mX != 0 && mX != width -1 && mY == 0 -> {
            roundIndexResult[0] = INVALID_INDEX
            roundIndexResult[1] = INVALID_INDEX
            roundIndexResult[2] = INVALID_INDEX
            roundIndexResult[3] = meIndex + 1
            roundIndexResult[4] = meIndex + width + 1
            roundIndexResult[5] = meIndex + width
            roundIndexResult[6] = meIndex + width - 1
            roundIndexResult[7] = meIndex - 1
        }
        mX == 0 && mY != 0 && mY != height - 1 -> {
            roundIndexResult[0] = INVALID_INDEX
            roundIndexResult[1] = meIndex - width
            roundIndexResult[2] = meIndex - width + 1
            roundIndexResult[3] = meIndex + 1
            roundIndexResult[4] = meIndex + width + 1
            roundIndexResult[5] = meIndex + width
            roundIndexResult[6] = INVALID_INDEX
            roundIndexResult[7] = INVALID_INDEX
        }

        mX == width - 1 && mY != 0 && mY != height - 1 -> {
            roundIndexResult[0] = meIndex - width - 1
            roundIndexResult[1] = meIndex - width
            roundIndexResult[2] = INVALID_INDEX
            roundIndexResult[3] = INVALID_INDEX
            roundIndexResult[4] = INVALID_INDEX
            roundIndexResult[5] = meIndex + width
            roundIndexResult[6] = meIndex + width - 1
            roundIndexResult[7] = meIndex - 1
        }

        mX != 0 && mX != width -1 && mY == height - 1 -> {
            roundIndexResult[0] = meIndex - width - 1
            roundIndexResult[1] = meIndex - width
            roundIndexResult[2] = meIndex - width + 1
            roundIndexResult[3] = meIndex + 1
            roundIndexResult[4] = INVALID_INDEX
            roundIndexResult[5] = INVALID_INDEX
            roundIndexResult[6] = INVALID_INDEX
            roundIndexResult[7] = meIndex - 1
        }

        else -> {
            roundIndexResult[0] = meIndex - width - 1
            roundIndexResult[1] = meIndex - width
            roundIndexResult[2] = meIndex - width + 1
            roundIndexResult[3] = meIndex + 1
            roundIndexResult[4] = meIndex + width + 1
            roundIndexResult[5] = meIndex + width
            roundIndexResult[6] = meIndex + width - 1
            roundIndexResult[7] = meIndex - 1
        }
    }
    return roundIndexResult
}

fun LifeModel2.getAroundAliveCount(meIndex: Int): Int {
    val roundIndexes = getRoundIndex(meIndex)
    var result: Int = 0
    var index = 0
    while (index > ROUND_INDEX_RESULT_COUNT - 1) {
        val roundIndex =  roundIndexes[index]
        if (roundIndex != INVALID_INDEX && life[roundIndex].isAlive) {
            result ++
        }
        index ++
    }
    return result
}