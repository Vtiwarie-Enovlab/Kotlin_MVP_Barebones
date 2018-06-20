package com.enovlab.yoop.ui.widget

import android.content.Context
import android.graphics.*
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.TypedValue
import com.enovlab.yoop.R

class EmptyCircleImageView : AppCompatImageView {

    private var circleRadiusParent = false
    private var circleRadius = 0f
    private var maxCircleRadius = 0f
    private var circlePadding = 0f
    private var circleStart = 0f
    private var borderWidth = 0f
    @ColorInt private var borderColor = 0

    private lateinit var circlePaint: Paint
    private var borderPaint: Paint? = null
    private var circleX = 0f
    private var circleY = 0f

    @ColorInt private var overlayColor: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyCircleImageView, defStyleAttr, 0)

        circleRadiusParent = a.getBoolean(R.styleable.EmptyCircleImageView_ec_circleRadiusParent, false)
        circleRadius = a.getDimension(R.styleable.EmptyCircleImageView_ec_circleRadius, 0f)
        maxCircleRadius = a.getDimension(R.styleable.EmptyCircleImageView_ec_maxCircleRadius, 0f)
        circlePadding = a.getDimension(R.styleable.EmptyCircleImageView_ec_circlePadding, 0f)
        circleStart = a.getDimension(R.styleable.EmptyCircleImageView_ec_circleStart, 0f)
        borderWidth = a.getDimension(R.styleable.EmptyCircleImageView_ec_borderWidth, 0f)
        borderColor = a.getColor(R.styleable.EmptyCircleImageView_ec_borderColor, fetchAccentColor())

        a.recycle()

        init()
    }

    private fun init() {
        createClearPaint()
        createBorderPaint()

        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec).toFloat() / 2f
        if (circleRadiusParent) {
            circleRadius = w
        }

        if (circleRadius > 0) {
            if (circlePadding > 0) circleRadius -= circlePadding
            if (borderWidth > 0) circleRadius -= borderWidth / 2f

            circleX = w
            circleY = circleStart + circleRadius
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (circleRadius > 0) {
            canvas.drawCircle(circleX, circleY, circleRadius, circlePaint)
        }

        if (borderWidth > 0) {
            canvas.drawCircle(circleX, circleY, circleRadius, borderPaint)
        }
    }

    fun setOverlayColor(@ColorRes color: Int) {
        overlayColor = ContextCompat.getColor(context, color)
    }

    fun setBorderColor(@ColorRes color: Int) {
        borderColor = ContextCompat.getColor(context, color)
        createBorderPaint()
        invalidate()
    }

    fun showOverlay(active: Boolean) {
        if (overlayColor != 0) {
            when {
                active -> createOverlayedPaint()
                else -> createClearPaint()
            }
            invalidate()
        }
    }

    fun circleSize() = circleRadius.toInt() * 2

    private fun createClearPaint() {
        circlePaint = Paint()
        circlePaint.color = Color.TRANSPARENT
        circlePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private fun createOverlayedPaint() {
        circlePaint = Paint()
        circlePaint.color = overlayColor
        circlePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    private fun createBorderPaint() {
        if (borderWidth > 0) {
            borderPaint = Paint()
            borderPaint?.color = borderColor
            borderPaint?.style = Paint.Style.STROKE
            borderPaint?.isAntiAlias = true
            borderPaint?.strokeWidth = borderWidth
        }
    }

    private fun fetchAccentColor(): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }
}