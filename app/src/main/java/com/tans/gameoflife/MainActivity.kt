package com.tans.gameoflife

import android.content.Intent
import com.tans.gameoflife.game.DefaultRule
import com.tans.gameoflife.game.LifeModel
import com.tans.gameoflife.game.Size
import com.tans.gameoflife.game.randomLife
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

            val defaultLife = Size(50, 50).randomLife(20, System.currentTimeMillis())
            game_view.lifeModel = defaultLife
            state.life.send(defaultLife)
            launch {
                while (!state.isPaused.asFlow().filter { !it }.first()) {
                    delay(globalSettingsState.speed.asFlow().first())
                    val oldLife = state.life.asFlow().first()
                    val newLife = DefaultRule(oldLife)
                    state.life.send(newLife)
                    game_view.lifeModel = newLife
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

            globalSettingsState.size.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    map_size_tv.text = "Map Size: $it"
                }

            globalSettingsState.type.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    type_tv.text = it.toString()
                }

            globalSettingsState.speed.asFlow()
                .distinctUntilChanged()
                .collectInCoroutine(this) {
                    speed_tv.text = "Speed: ${it}ms"
                }
        }
    }

    override fun onStop() {
        super.onStop()
        state.isPaused::send.executeInCoroutine(this, true)
    }

}

data class MainActivityState(
    val isPaused: BroadcastChannel<Boolean> = BroadcastChannel(Channel.CONFLATED),
    val life: BroadcastChannel<LifeModel> = BroadcastChannel(Channel.CONFLATED)
)
