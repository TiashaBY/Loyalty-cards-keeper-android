package com.rsschool.myapplication.loyaltycards.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import com.rsschool.myapplication.loyaltycards.R

class CustomRectangleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var backgroundColour = 0
    private val paint: Paint by lazy { Paint().apply { color = backgroundColour } }

    private val transparentPaint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        color = Color.TRANSPARENT
        isAntiAlias = true
    }

    private val strokePaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 8F
    }

    init {
        var styledAttrs: TypedArray? = null
        if (attrs != null) {
            try {
                styledAttrs = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomRectangleView,
                    defStyleAttr,
                    0
                )
                backgroundColour =
                    styledAttrs.getColor(R.styleable.CustomRectangleView_custom_color, Color.BLACK)
            } finally {
                styledAttrs?.recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val widthReal = width.toFloat()
        val heightReal = height.toFloat()

        var croppedWidth = 0F
        var croppedHeight = 0F
        val standardCardHeight = 54
        val standardCardWidth = 86
        if (heightReal > widthReal) {
            croppedWidth = widthReal * 0.9F
            croppedHeight = (widthReal * standardCardHeight) / standardCardWidth
        } else {
            croppedHeight = heightReal * 0.9F
            croppedWidth = (croppedHeight * standardCardWidth) / standardCardHeight
        }
        val topLeftX = (widthReal - croppedWidth) / 2
        val topLeftY = (heightReal - croppedHeight) / 2
        val topRightX = (topLeftX + croppedWidth)
        val topRightY = topLeftY + croppedHeight
        // Set rect centered in frame
        val rect = RectF(topLeftX, topLeftY, topRightX, topRightY)

        //draw black background
        val cornerRadius = 25f
        canvas.drawPaint(paint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, transparentPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
    }
}
