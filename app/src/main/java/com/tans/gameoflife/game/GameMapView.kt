package com.tans.gameoflife.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import kotlin.math.min

class GameMapView : View {

    var lifeModel: LifeModel? = null
    set(value) {
        field = value
        ViewCompat.postInvalidateOnAnimation(this)
    }

    val drawBorder: Boolean = false
//    set(value) {
//        field = value
//        ViewCompat.postInvalidateOnAnimation(this)
//    }

    var sizeChangeListener: ((width: Int, height: Int) -> Unit)? = null

    val borderSize: Int = 1

    val lineColor = Color.BLACK

    val cellColor = Color.BLACK

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
        val drawBorder = this.drawBorder
        val borderSize = if (drawBorder) {
            borderSize
        } else {
            0
        }
        val mapSize = lifeModel?.mapSize
        val life = lifeModel?.life
        if (canvas != null
            && mapSize != null
            && life != null) {
            val mapScreenWidth = measuredWidth.let { screenWidth ->
                screenWidth  - (screenWidth - borderSize) % mapSize.width
            }
            val mapScreenHeight = measuredHeight.let { screenHeight ->
                screenHeight  - (screenHeight - borderSize) % mapSize.height
            }
            val cellSize = min((mapScreenWidth - borderSize) / mapSize.width, (mapScreenHeight - borderSize) / mapSize.height) - borderSize
            if (cellSize >= 1) {
                if (drawBorder) {
                    val mapWithInPixel = cellSize * mapSize.width + (mapSize.width + 1) * borderSize
                    val mapHeightPixel = cellSize * mapSize.height + (mapSize.height + 1) * borderSize
                    repeat(mapSize.width + 1) { i ->
                        canvas.drawLine(
                            (i * (cellSize + borderSize)).toFloat(), 0f,
                            (i * (cellSize + borderSize)).toFloat(), mapHeightPixel.toFloat(), linePaint
                        )
                    }

                    repeat((mapSize.height + 1)) { i ->
                        canvas.drawLine(
                            0f, (i * (cellSize + borderSize)).toFloat(),
                            mapWithInPixel.toFloat(), (i * (cellSize + borderSize)).toFloat(), linePaint
                        )
                    }
                }

                synchronized(lifeModel) {
                    repeat(life.size) { index ->
                        val alive = life[index].isAlive
                        if (alive) {
                            val x = life[index].x
                            val y = life[index].y
                            val startX = (x * (cellSize + borderSize) + borderSize).toFloat()
                            val startY = (y * (cellSize + borderSize) + borderSize).toFloat()
                            canvas.drawRect(startX, startY,
                                startX + cellSize, startY + cellSize, cellPaint)
                        }
                    }
                }

            }
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidate()
        sizeChangeListener?.invoke(w, h)
    }

}