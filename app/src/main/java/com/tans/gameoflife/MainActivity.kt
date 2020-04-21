package com.tans.gameoflife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launch {
            repeat(20) {
                delay(1000)
                count_tv.text = (it + 1).toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }
}
