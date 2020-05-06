package com.tans.gameoflife.game

import androidx.annotation.IntRange
import kotlin.random.Random
import kotlin.random.nextInt

fun Size.randomLife(@IntRange(from = 0L, to = 100L) probability: Int, seed: Long): LifeModel {
    return LifeModel(
        mapSize = this,
        life = MutableList(width * height) { index ->
            val isAlive = probability > Random(seed - probability * index * 100).nextInt(1..100)
            val x = index % width
            val y = index / width
            Cell(x = x, y = y, isAlive = isAlive)
        },
        aliveLifeCache = MutableList(width * height) { false }
    )
}

sealed class RoundPoint {
    abstract fun index(meIndex: Int, width: Int, height: Int): Int

    object Point0 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex - width - 1
    }

    object Point1 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex - width
    }

    object Point2 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex - width + 1
    }

    object Point3 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex + 1
    }

    object Point4 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex + width + 1
    }

    object Point5 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex + width
    }

    object Point6 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex + width - 1
    }

    object Point7 : RoundPoint() {
        override fun index(meIndex: Int, width: Int, height: Int): Int = meIndex - 1
    }

    object Invalid : RoundPoint() { override fun index(meIndex: Int, width: Int, height: Int): Int = INVALID_INDEX }
}

const val INVALID_INDEX: Int = -1
const val ROUND_INDEX_RESULT_COUNT = 8
val roundIndexResult: MutableList<RoundPoint> = MutableList(ROUND_INDEX_RESULT_COUNT) { RoundPoint.Invalid }
fun LifeModel.getRoundIndex(meIndex: Int): List<RoundPoint> {
    val width = mapSize.width
    val height = mapSize.height
    val mX = life[meIndex].x
    val mY = life[meIndex].y
    when {
        mX == 0 && mY == 0 -> {
            roundIndexResult[0] = RoundPoint.Invalid
            roundIndexResult[1] = RoundPoint.Invalid
            roundIndexResult[2] = RoundPoint.Invalid
            roundIndexResult[3] = RoundPoint.Point3
            roundIndexResult[4] = RoundPoint.Point4
            roundIndexResult[5] = RoundPoint.Point5
            roundIndexResult[6] = RoundPoint.Invalid
            roundIndexResult[7] = RoundPoint.Invalid
        }
        mX == (width - 1) && mY == 0 -> {
            roundIndexResult[0] = RoundPoint.Invalid
            roundIndexResult[1] = RoundPoint.Invalid
            roundIndexResult[2] = RoundPoint.Invalid
            roundIndexResult[3] = RoundPoint.Invalid
            roundIndexResult[4] = RoundPoint.Invalid
            roundIndexResult[5] = RoundPoint.Point5
            roundIndexResult[6] = RoundPoint.Point6
            roundIndexResult[7] = RoundPoint.Point7
        }
        mX == 0 && mY == height - 1 -> {
            roundIndexResult[0] = RoundPoint.Invalid
            roundIndexResult[1] = RoundPoint.Point1
            roundIndexResult[2] = RoundPoint.Point2
            roundIndexResult[3] = RoundPoint.Point3
            roundIndexResult[4] = RoundPoint.Invalid
            roundIndexResult[5] = RoundPoint.Invalid
            roundIndexResult[6] = RoundPoint.Invalid
            roundIndexResult[7] = RoundPoint.Invalid
        }
        mX == width - 1 && mY == height - 1 -> {
            roundIndexResult[0] = RoundPoint.Point0
            roundIndexResult[1] = RoundPoint.Point1
            roundIndexResult[2] = RoundPoint.Invalid
            roundIndexResult[3] = RoundPoint.Invalid
            roundIndexResult[4] = RoundPoint.Invalid
            roundIndexResult[5] = RoundPoint.Invalid
            roundIndexResult[6] = RoundPoint.Invalid
            roundIndexResult[7] = RoundPoint.Point7
        }
        mX != 0 && mX != width -1 && mY == 0 -> {
            roundIndexResult[0] = RoundPoint.Invalid
            roundIndexResult[1] = RoundPoint.Invalid
            roundIndexResult[2] = RoundPoint.Invalid
            roundIndexResult[3] = RoundPoint.Point3
            roundIndexResult[4] = RoundPoint.Point4
            roundIndexResult[5] = RoundPoint.Point5
            roundIndexResult[6] = RoundPoint.Point6
            roundIndexResult[7] = RoundPoint.Point7
        }
        mX == 0 && mY != 0 && mY != height - 1 -> {
            roundIndexResult[0] = RoundPoint.Invalid
            roundIndexResult[1] = RoundPoint.Point1
            roundIndexResult[2] = RoundPoint.Point2
            roundIndexResult[3] = RoundPoint.Point3
            roundIndexResult[4] = RoundPoint.Point4
            roundIndexResult[5] = RoundPoint.Point5
            roundIndexResult[6] = RoundPoint.Invalid
            roundIndexResult[7] = RoundPoint.Invalid
        }

        mX == width - 1 && mY != 0 && mY != height - 1 -> {
            roundIndexResult[0] = RoundPoint.Point0
            roundIndexResult[1] = RoundPoint.Point1
            roundIndexResult[2] = RoundPoint.Invalid
            roundIndexResult[3] = RoundPoint.Invalid
            roundIndexResult[4] = RoundPoint.Invalid
            roundIndexResult[5] = RoundPoint.Point5
            roundIndexResult[6] = RoundPoint.Point6
            roundIndexResult[7] = RoundPoint.Point7
        }

        mX != 0 && mX != width -1 && mY == height - 1 -> {
            roundIndexResult[0] = RoundPoint.Point0
            roundIndexResult[1] = RoundPoint.Point1
            roundIndexResult[2] = RoundPoint.Point2
            roundIndexResult[3] = RoundPoint.Point3
            roundIndexResult[4] = RoundPoint.Invalid
            roundIndexResult[5] = RoundPoint.Invalid
            roundIndexResult[6] = RoundPoint.Invalid
            roundIndexResult[7] = RoundPoint.Point7
        }

        else -> {
            roundIndexResult[0] = RoundPoint.Point0
            roundIndexResult[1] = RoundPoint.Point1
            roundIndexResult[2] = RoundPoint.Point2
            roundIndexResult[3] = RoundPoint.Point3
            roundIndexResult[4] = RoundPoint.Point4
            roundIndexResult[5] = RoundPoint.Point5
            roundIndexResult[6] = RoundPoint.Point6
            roundIndexResult[7] = RoundPoint.Point7
        }
    }
    return roundIndexResult
}

fun LifeModel.getAroundAliveCount(meIndex: Int): Int {
    val roundIndexes = getRoundIndex(meIndex)
    val size: Size = mapSize
    var result: Int = 0
    var index = 0
    while (index < ROUND_INDEX_RESULT_COUNT) {
        val roundIndex = roundIndexes[index].index(meIndex, size.width, size.height)
        if (roundIndex != INVALID_INDEX && life[roundIndex].isAlive) {
            result++
        }
        index++
    }
    return result
}