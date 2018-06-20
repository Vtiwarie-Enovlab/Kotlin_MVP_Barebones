package com.enovlab.yoop.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.graphics.drawable.Animatable2Compat
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.enovlab.yoop.R

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
open class MyRequestsButton : AppCompatButton {

    private lateinit var arrow: AnimatedVectorDrawableCompat
    private var rotated = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun rotate() {
        arrow.start()
    }

    fun rotateDown() {
        if (rotated) rotate()
    }

    fun rotateUp() {
        if (!rotated) rotate()
    }

    private fun init() {
        updateDrawable()
    }

    override fun performClick(): Boolean {
        rotate()
        return super.performClick()
    }

    private fun updateDrawable() {
        arrow = AnimatedVectorDrawableCompat.create(context, when {
            rotated -> R.drawable.transactions_arrow_animation_up
            else -> R.drawable.transactions_arrow_animation_down
        })!!
        arrow.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                rotated = !rotated
                updateDrawable()
            }
        })

        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, arrow, null)
    }
}