package com.tans.gameoflife

import android.content.Intent
import com.tans.gameoflife.game.DefaultRule
import com.tans.gameoflife.game.LifeModel
import com.tans.gameoflife.game.Size
import com.tans.gameoflife.game.randomLife
import com.tans.gameoflife.settings.GameLaunchType
import com.tans.gameoflife.settings.globalSettingsState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class MainActivity : BaseActivity() {

    val state: MainActivityState = MainActivityState()
    override val layoutRes: Int = R.layout.activity_main

    override fun initData() {
        launch {
            // Init default state.
            state.isPaused.send(true)
            val launchType = globalSettingsState.gameLaunchType.asFlow().first()
            val lifeModel = when (launchType) {
                is GameLaunchType.Random -> {
                    launchType.refreshInitLifeModel(System.currentTimeMillis())
                }
                else -> null
            }

            launch {
                while (!state.isPaused.asFlow().filter { !it }.first()) {
                    val launchTypeLocal = globalSettingsState.gameLaunchType.asFlow().first()
                    delay(launchTypeLocal.speed)
                    if (lifeModel != null) {
                        launchTypeLocal.rule(lifeModel)
                    }
                }
            }

        }
    }

    override fun initViews() {
        launch {
            start_pause_bt.clicks()
                .collectInCoroutine(this) {
                    val isPaused = state.isPaused.asFlow().first()
                    state.isPaused.send(!isPaused)
                }

            settings_bt.clicks()
                .collectInCoroutine(this) {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }

            state.isPaused.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) { isPaused ->
                    if (isPaused) {
                        start_pause_bt.text = getString(R.string.start)
                        settings_bt.isEnabled = true
                    } else {
                        start_pause_bt.text = getString(R.string.pause)
                        settings_bt.isEnabled = false
                    }
                }

//            state.life.asFlow()
//                .distinctUntilChanged()
//                .collectInCoroutine(this) { life -> game_view.lifeModel = life }

            globalSettingsState.gameLaunchType.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) { type ->
                    state.isPaused.send(true)
                    map_size_tv.text = "Map Size: ${type.mapSize}"
                }

            globalSettingsState.gameLaunchType.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    type_tv.text = it.toString()
                }

            globalSettingsState.gameLaunchType.asFlow()
                .map { it.speed }
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    speed_tv.text = "Speed: ${it}ms"
                }

            globalSettingsState.showBorder.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    game_view.drawBorder = it
                    state.isPaused.send(true)
                }
        }
    }

    override fun onStop() {
        super.onStop()
        state.isPaused::send.executeInCoroutine(this, true)
    }

}

data class MainActivityState(
    val isPaused: BroadcastChannel<Boolean> = BroadcastChannel(Channel.CONFLATED)
//    val life: BroadcastChannel<LifeModel> = BroadcastChannel(Channel.CONFLATED)
)
