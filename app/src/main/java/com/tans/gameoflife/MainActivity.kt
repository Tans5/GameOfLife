package com.tans.gameoflife

import android.content.Intent
import com.tans.gameoflife.settings.globalSettingsState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class MainActivity : BaseActivity() {

    val state: MainActivityState = MainActivityState()
    override val layoutRes: Int = R.layout.activity_main

    init {
        activityLatestLifeStateChannel.asFlow()
            .collectInCoroutine(this) {
                println("Life: $it")
            }
    }

    override fun initData() {}

    override fun initViews() {
        var gameJob: Job? = null

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


            globalSettingsState.gameLaunchType.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) { type ->
                    state.isPaused.send(true)
                    map_size_tv.text = "Map Size: ${type.mapSize}"
                }

            activityLatestLifeStateChannel.asFlow()
                .filter {
                    println("life: $it")
                    it == ActivityLife.OnResume
                }
                .map { globalSettingsState.gameLaunchType.asFlow().first() }
                .distinctUntilChanged { old, new ->
                    when {
                        old.mapSize != new.mapSize -> false
                        old::class != new::class -> false
                        else -> true
                    }
                }
                .collectInCoroutine(this) {
                    gameJob?.cancelAndJoin()
                    gameJob = startGame()
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
                    state.isPaused.send(true)
                    game_view.drawBorder = it
                }
        }
    }

    fun startGame(): Job = launch {
        state.isPaused.send(true)
        val launchType = globalSettingsState.gameLaunchType.asFlow().first()
        val lifeModel = launchType.refresh()
        game_view.lifeModel = lifeModel
        launch(Dispatchers.IO) {
            while (!state.isPaused.asFlow().filter { !it }.first()) {
                val launchTypeLocal = globalSettingsState.gameLaunchType.asFlow().first()
                launchTypeLocal.rule(lifeModel)
                game_view.postInvalidateOnAnimation(launchTypeLocal.speed)
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
)
