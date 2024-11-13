package com.myapp.viewport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Path
import android.util.AttributeSet
import android.view.ViewGroup

class Viewport : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // No child layouts to position
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val viewportMargin = 32 // Margin around the square
        val viewportCornerRadius = 8

        // Set up paint for clearing the area
        val eraser = Paint().apply {
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        // Calculate the size of the square after considering margins
        val availableWidth = width - 2 * viewportMargin
        val squareSize = availableWidth.coerceAtMost(
            (height - 2 * viewportMargin).toFloat().toInt()
        ) // Make it a square

        // Center the square horizontally and vertically
        val left = (this.width - squareSize) / 4f // Center horizontally
        val top = (this.height - squareSize) / 2f // Center vertically

        // Create the square and its border frame with margins applied
        val rect = RectF(
            left + viewportMargin,
            top + viewportMargin,
            left + squareSize,
            top + squareSize
        )
        val frame = RectF(
            left + viewportMargin - 2,
            top + viewportMargin - 2,
            left + squareSize + 4,
            top + squareSize + 4
        )

        val path = Path()
        val stroke = Paint().apply {
            isAntiAlias = true
            strokeWidth = 4f
            color = Color.WHITE
            style = Paint.Style.STROKE
        }

        // Draw the border (stroke)
        path.addRoundRect(
            frame,
            viewportCornerRadius.toFloat(),
            viewportCornerRadius.toFloat(),
            Path.Direction.CW
        )
        canvas.drawPath(path, stroke)

        // Clear the inside area (transparent square)
        canvas.drawRoundRect(
            rect,
            viewportCornerRadius.toFloat(),
            viewportCornerRadius.toFloat(),
            eraser
        )
    }
}
