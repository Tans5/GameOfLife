package com.tans.gameoflife

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class MainActivity : BaseActivity() {

    val state: MainActivityState = MainActivityState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init data
        launch {
            // Init default state.
            state.isPaused.send(false)
            state.count.send(0)

            launch {
                while (!state.isPaused.receiveAsFlow().filter { !it }.first()) {
                    val count = state.count.receiveAsFlow().first()
                    delay(1000)
                    state.count.send(count + 1)
                }
            }

        }

        // init UI
        launch {

            launch {
                start_pause_bt.clicks()
                    .collect {
                        println("before: click me")
                        val isPaused = state.isPaused.receiveAsFlow().first()
                        println("after: click me: $isPaused")
                        state.isPaused.send(!isPaused)
                    }
            }

            launch {
                state.isPaused.receiveAsFlow()
                    .distinctUntilChanged()
                    .collect { isPaused ->
                        if (isPaused) {
                            start_pause_bt.text = getString(R.string.pause)
                        } else {
                            start_pause_bt.text = getString(R.string.start)
                        }
                    }
            }

            launch {
                state.count.receiveAsFlow()
                    .collect {
                        count_tv.text = it.toString()
                    }
            }
        }
    }
}

data class MainActivityState(
    val isPaused: Channel<Boolean> = Channel(Channel.CONFLATED),
    val count: Channel<Int> = Channel(Channel.CONFLATED)
)
