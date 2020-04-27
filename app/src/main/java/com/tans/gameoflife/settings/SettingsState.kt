package com.tans.gameoflife.settings

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.tans.gameoflife.game.Size
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import java.math.RoundingMode

sealed class GameInitType {
    data class Random(@IntRange(from = 0L, to = 100L) val eachCellProbability: Int) : GameInitType() {
        override fun toString(): String {
            return "Random, Probability: $eachCellProbability"
        }
    }
    data class Local(val file: String) : GameInitType()
}

/**
 * @size: default: 50 * 50, max: 900 * 900, min: 50 * 50
 * @type: default: Random
 * @speed: default 100ms, max: 1000, min: 100ms
 */
data class SettingsState(
    val size: BroadcastChannel<Size> = BroadcastChannel(Channel.CONFLATED),
    val type: BroadcastChannel<GameInitType> = BroadcastChannel(Channel.CONFLATED),
    val speed: BroadcastChannel<Long> = BroadcastChannel(Channel.CONFLATED)
)

val globalSettingsState = SettingsState()

const val SIZE_MAX: Int = 200
const val SIZE_MIN: Int = 10
const val SPEED_MAX: Long = 100
const val SPEED_MIN: Long = 1000
const val CELL_ALIVE_PROBABILITY_MAX: Int = 100
const val CELL_ALIVE_PROBABILITY_MIN: Int = 0