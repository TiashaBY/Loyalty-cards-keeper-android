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
    private var paint: Paint? = null
    private var transparentPaint: Paint? = null

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

            paint = Paint()
            paint?.color = backgroundColour

            transparentPaint = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
                color = Color.TRANSPARENT
                isAntiAlias = true
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

        val cornerRadius = 25f
        // Set rect centered in frame
        val rect = RectF(
            topLeftX,
            topLeftY,
            (topLeftX + croppedWidth),
            topLeftY + croppedHeight
        )

        //draw blackbackground
        paint?.let { canvas.drawPaint(it) }

        transparentPaint?.let {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, it)
        }
    }
}
