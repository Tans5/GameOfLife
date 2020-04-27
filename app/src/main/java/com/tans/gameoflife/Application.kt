package com.tans.gameoflife

import android.app.Application
import com.tans.gameoflife.game.Size
import com.tans.gameoflife.settings.GameInitType
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
            globalSettingsState.size.send(Size(50, 50))
            globalSettingsState.speed.send(100)
            globalSettingsState.type.send(GameInitType.Random(10))
        }
    }

}