package com.tans.gameoflife

import android.app.Application
import com.tans.gameoflife.game.DEFAULT_GOLLY_CODE
import com.tans.gameoflife.game.DefaultRule
import com.tans.gameoflife.game.GollyCodeParser
import com.tans.gameoflife.game.Size
import com.tans.gameoflife.settings.GameLaunchType
import com.tans.gameoflife.settings.SettingsState
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
            val (lifeModel, rule) = GollyCodeParser(DEFAULT_GOLLY_CODE[0])
            globalSettingsState.gameLaunchType.send(GameLaunchType.Common(initLifeModel = lifeModel, rule = rule, mapSize = lifeModel.mapSize, speed = 100))
            globalSettingsState.showBorder.send(false)
        }
    }

}