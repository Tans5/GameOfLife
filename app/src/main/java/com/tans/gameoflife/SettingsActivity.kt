package com.tans.gameoflife

import com.tans.gameoflife.game.Size
import com.tans.gameoflife.settings.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.min

class SettingsActivity : BaseActivity() {


    override val layoutRes: Int = R.layout.activity_settings

    override fun initData() {
    }

    override fun initViews() {

        launch {
            map_size_sb.progress = globalSettingsState.gameLaunchType.asFlow().map { it.mapSize }.first().sizeToProgress()
            speed_sb.progress = globalSettingsState.gameLaunchType.asFlow().map { it.speed }.first().speedToProgress()
            // TODO: Now GameLaunchType only support Random
            probability_sb.progress = globalSettingsState.gameLaunchType.asFlow().map { (it as? GameLaunchType.Random)?.eachCellProbability ?: 0 }
                .first().probabilityToProgress()

            show_border_switch.isChecked = globalSettingsState.showBorder.asFlow().first()

            map_size_sb.progressChange()
                .collectInCoroutine(this) { progress ->
                    when (val oldLaunchType = globalSettingsState.gameLaunchType.asFlow().first()) {
                        is GameLaunchType.Random -> {
                            val newLaunchType = oldLaunchType.copy(mapSize = progress.progressToSize())
                            globalSettingsState.gameLaunchType.send(newLaunchType)
                        }
                    }

                }

            speed_sb.progressChange()
                .collectInCoroutine(this) { progress ->
                    when (val oldLaunchType = globalSettingsState.gameLaunchType.asFlow().first()) {
                        is GameLaunchType.Random -> {
                            val newLaunchType = oldLaunchType.copy(speed = progress.progressToSpeed())
                            globalSettingsState.gameLaunchType.send(newLaunchType)
                        }
                    }
                }

            probability_sb.progressChange()
                .collectInCoroutine(this) { progress ->

                    when (val oldLaunchType = globalSettingsState.gameLaunchType.asFlow().first()) {
                        is GameLaunchType.Random -> {
                            val newLaunchType = oldLaunchType.copy(eachCellProbability = progress.progressToProbability())
                            globalSettingsState.gameLaunchType.send(newLaunchType)
                        }
                    }
                }

            show_border_switch.checkChanges()
                .distinctUntilChanged()
                .collectInCoroutine(this) { globalSettingsState.showBorder.send(it) }

            globalSettingsState.gameLaunchType.asFlow()
                .map { it.mapSize }
                .distinctUntilChanged()
                .collectInCoroutine(this) { map_size_result_tv.text = getString(R.string.map_size_value, it.width, it.height) }

            globalSettingsState.gameLaunchType.asFlow()
                .map { it.speed }
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    speed_result_tv.text = getString(R.string.speed_value, it)
                }

            globalSettingsState.gameLaunchType.asFlow()
                .map {
                    (it as? GameLaunchType.Random)?.eachCellProbability ?: 0
                }
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    probability_result_tv.text = it.toString()
                }
        }
    }

    companion object {

        fun Size.sizeToProgress(): Int {
            val result = (((min(width, height) - SIZE_MIN) * 100).toFloat() / ((SIZE_MAX - SIZE_MIN).toFloat()) + 0.5).toInt()
            return if (result < 0) {
                error("Wrong Progress Size: $this")
            } else {
                result
            }
        }

        fun Int.progressToSize(): Size = if (this !in 0..100) {
            error("Wrong Progress: $this")
        } else {
            val size = (((this.toFloat() / 100f) * (SIZE_MAX - SIZE_MIN)) + 0.5).toInt() + SIZE_MIN
            Size(width = size, height = size)
        }

        fun Long.speedToProgress(): Int = if (this !in SPEED_MAX .. SPEED_MIN) {
            error("Wrong Speed: $this")
        } else {
            (((1f - (this - SPEED_MAX).toFloat() / (SPEED_MIN - SPEED_MAX).toFloat()) * 100) + 0.5f).toInt()
        }

        fun Int.progressToSpeed(): Long = if (this !in 0..100) {
            error("Wrong Progress: $this")
        } else {
            (SPEED_MIN.toFloat() - ((SPEED_MIN - SPEED_MAX) * this).toFloat() / 100f + 0.5f).toLong()
        }

        fun Int.probabilityToProgress(): Int = if (this !in CELL_ALIVE_PROBABILITY_MIN..CELL_ALIVE_PROBABILITY_MAX) {
            error("Wrong Probability: $this")
        } else {
            (((this - CELL_ALIVE_PROBABILITY_MIN) / (CELL_ALIVE_PROBABILITY_MAX - CELL_ALIVE_PROBABILITY_MIN)) * 100 + 0.5f).toInt()
        }

        fun Int.progressToProbability(): Int = if (this !in 0..100) {
            error("Wrong Progress: $this")
        } else {
            ((this.toFloat() / 100f) * (CELL_ALIVE_PROBABILITY_MAX - CELL_ALIVE_PROBABILITY_MIN) + CELL_ALIVE_PROBABILITY_MIN + 0.5f).toInt()
        }
    }

}