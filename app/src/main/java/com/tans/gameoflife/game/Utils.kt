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