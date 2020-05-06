package com.tans.gameoflife

import android.app.Application
import com.tans.gameoflife.game.DefaultRule
import com.tans.gameoflife.game.Size
import com.tans.gameoflife.settings.GameLaunchType
import com.tans.gameoflife.settings.globalSettingsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Application : Application(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    override fun onCreate() {
        super.onCreate()

        // Init Settings
        launch {
            globalSettingsState.gameLaunchType.send(GameLaunchType.Random(10, rule = DefaultRule, mapSize = Size(50, 50), speed = 100))
            globalSettingsState.showBorder.send(false)
        }
    }

}