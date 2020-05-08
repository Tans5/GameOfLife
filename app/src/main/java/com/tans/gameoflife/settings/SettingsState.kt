package com.tans.gameoflife.settings

import androidx.annotation.IntRange
import com.tans.gameoflife.game.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

sealed class GameLaunchType(open val rule: Rule, open val mapSize: Size, open val speed: Long) {

    fun nextLife(lifeModel: LifeModel) { rule(lifeModel) }

    abstract fun refresh(): LifeModel

    data class Random(@IntRange(from = 0L, to = 100L) val eachCellProbability: Int,
                      override val rule: Rule,
                      override val mapSize: Size,
                      override val speed: Long) : GameLaunchType(rule, mapSize, speed) {
        override fun toString(): String {
            return "Random, Probability: $eachCellProbability"
        }

        /**
         * side effect.
         */
        override fun refresh(): LifeModel {
            return mapSize.randomLife(eachCellProbability, System.currentTimeMillis())
        }
    }

    data class Common(val initLifeModel: LifeModel,
                        override val rule: Rule,
                        override val mapSize: Size,
                        override val speed: Long) : GameLaunchType(rule, mapSize, speed) {

        override fun refresh(): LifeModel {
            return LifeModel(
                mapSize = initLifeModel.mapSize,
                life = MutableList(initLifeModel.mapSize.width * initLifeModel.mapSize.height) { initLifeModel.life[it].copy() }
            )
        }

        override fun toString(): String {
            return "Common"
        }
    }
}

/**
 * @size: default: 50 * 50, max: 900 * 900, min: 50 * 50
 * @type: default: Random
 * @speed: default 100ms, max: 1000, min: 100ms
 */
data class SettingsState(
    val gameLaunchType: BroadcastChannel<GameLaunchType> = BroadcastChannel(Channel.CONFLATED)
    // ,
    // val showBorder: BroadcastChannel<Boolean> = BroadcastChannel(Channel.CONFLATED)
)

val globalSettingsState = SettingsState()

const val SIZE_MAX: Int = 500
const val SIZE_MIN: Int = 10
const val SPEED_MAX: Long = 10
const val SPEED_MIN: Long = 1000
const val CELL_ALIVE_PROBABILITY_MAX: Int = 100
const val CELL_ALIVE_PROBABILITY_MIN: Int = 0