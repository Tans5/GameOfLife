package com.tans.gameoflife

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import java.lang.ref.WeakReference
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {



    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job() + AndroidCoroutineContext(WeakReference(this))

    abstract val layoutRes: Int

    // TODO: Contains some bug.
    val activityLatestLifeStateChannel: BroadcastChannel<ActivityLife> = BroadcastChannel(Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLatestLifeStateChannel::send.executeInCoroutine(this, ActivityLife.OnCreate)
        setContentView(layoutRes)
        initViews()
        initData()
    }

    override fun onStart() {
        super.onStart()
        activityLatestLifeStateChannel::send.executeInCoroutine(this, ActivityLife.OnStart)
    }

    override fun onResume() {
        super.onResume()
        activityLatestLifeStateChannel::send.executeInCoroutine(this, ActivityLife.OnResume)
    }

    override fun onPause() {
        super.onPause()
        activityLatestLifeStateChannel::send.executeInCoroutine(this, ActivityLife.OnPause)
    }

    override fun onStop() {
        super.onStop()
        activityLatestLifeStateChannel::send.executeInCoroutine(this, ActivityLife.OnStop)
    }

    abstract fun initData()

    abstract fun initViews()

    override fun onDestroy() {
        super.onDestroy()
        activityLatestLifeStateChannel::send.executeInCoroutine(this, ActivityLife.OnDestroy)
        activityLatestLifeStateChannel.close()
        cancel()
    }
}

class AndroidCoroutineContext(val context: WeakReference<Context>) : AbstractCoroutineContextElement(AndroidCoroutineContext) {

    companion object Key : CoroutineContext.Key<AndroidCoroutineContext>

}

enum class ActivityLife { OnCreate, OnStart, OnResume, OnPause, OnStop, OnDestroy }