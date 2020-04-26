package com.tans.gameoflife

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {



    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job() + AndroidCoroutineContext(WeakReference(this))

    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        initViews()
        initData()
    }

    abstract fun initData()

    abstract fun initViews()

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}

class AndroidCoroutineContext(val context: WeakReference<Context>) : AbstractCoroutineContextElement(AndroidCoroutineContext) {

    companion object Key : CoroutineContext.Key<AndroidCoroutineContext>

}