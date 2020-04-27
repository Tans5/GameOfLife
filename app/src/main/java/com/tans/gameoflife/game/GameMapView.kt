package com.tans.gameoflife.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import kotlin.math.max
import kotlin.math.min

class GameMapView : View {

    var lifeModel: LifeModel? = null
    set(value) {
        field = value
        ViewCompat.postInvalidateOnAnimation(this)
    }

    val lineColor = Color.BLACK

    val cellColor = Color.GREEN

    val linePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = lineColor
            style = Paint.Style.STROKE
        }
    }

    val cellPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = cellColor
            style = Paint.Style.FILL
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onDraw(canvas: Canvas?) {
        val lifeModel = this.lifeModel
        if (canvas != null
            && lifeModel != null
            && lifeModel.life.size == lifeModel.mapSize.height * lifeModel.mapSize.width) {
            val viewWidth = measuredWidth
            val viewHeight = measuredHeight
            val mapSize = lifeModel.mapSize
            val cellSize = (min((viewWidth - 1).toFloat() / mapSize.width.toFloat(), (viewHeight - 1).toFloat() / mapSize.height.toFloat())).toInt()
            if (cellSize >= 1) {
                val mapWithInPixel = cellSize * mapSize.width + mapSize.width + 1
                val mapHeightPixel = cellSize * mapSize.height + mapSize.height + 1
                repeat(mapSize.width + 1) { i ->
                    canvas.drawLine((i * (cellSize + 1)).toFloat(), 0f,
                        (i * (cellSize + 1)).toFloat(), mapHeightPixel.toFloat(), linePaint)
                }

                repeat((mapSize.height + 1)) { i ->
                    canvas.drawLine(0f, (i * (cellSize + 1)).toFloat(),
                        mapWithInPixel.toFloat(), (i * (cellSize + 1)).toFloat(), linePaint)
                }

                lifeModel.life.withIndex().forEach { (i, isAlive) ->
                    if (isAlive) {
                        val (x, y) = mapSize.getCoordinate(i)
                        canvas.drawRect((x * (cellSize + 1) + 1).toFloat(), (y * (cellSize + 1) + 1).toFloat(),
                            (x * (cellSize + 1) + cellSize).toFloat(), (y * (cellSize + 1)  + cellSize).toFloat(), cellPaint)
                    }
                }

            }
        }

    }

}