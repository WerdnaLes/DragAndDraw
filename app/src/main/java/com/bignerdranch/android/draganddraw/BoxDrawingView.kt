package com.bignerdranch.android.draganddraw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList

private const val TAG = "BoxDrawingView"

class BoxDrawingView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var currentBox: Box? = null
    private var boxes = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = 0x22ff0000
    }
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }


    @Suppress("UNCHECKED_CAST")
    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putParcelableArrayList("boxes", boxes as ArrayList<out Parcelable>)

        return bundle
    }

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState: Parcelable? = state
        if (state is Bundle) {
            val savedBoxes = state.getParcelableArrayList<Parcelable>("boxes")
            boxes = savedBoxes as MutableList<Box>
            newState = state.getParcelable("superState")
        }
        super.onRestoreInstanceState(newState)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawPaint(backgroundPaint)

            boxes.forEach { box ->
                canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val current = PointF(event.x, event.y)
            var action = ""
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    action = "ACTION_DOWN"
                    // Reset drawing state:
                    currentBox = Box(current).also {
                        boxes.add(it)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    action = "ACTION_MOVE"
                    updateCurrentBox(current)
                }
                MotionEvent.ACTION_UP -> {
                    action = "ACTION_UP"
                    updateCurrentBox(current)
                    currentBox = null
                }
                MotionEvent.ACTION_CANCEL -> {
                    action = "ACTION_CANCEL"
                    currentBox = null
                }
            }

            Log.i(TAG, "$action at x=${current.x}")

            return true
        } else return false
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            invalidate()
        }
    }
}