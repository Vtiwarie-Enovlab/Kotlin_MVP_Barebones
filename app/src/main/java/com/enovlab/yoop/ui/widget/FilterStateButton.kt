package com.enovlab.yoop.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.enovlab.yoop.R

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
open class FilterStateButton : AppCompatButton {

    private val colorActivated: Int
    private val colorNotActivated: Int

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.FilterStateButton, defStyleAttr, 0)
        val activeRes = a.getResourceId(R.styleable.FilterStateButton_fsb_colorActivated, 0)
        val deactiveRes = a.getResourceId(R.styleable.FilterStateButton_fsb_colorDeactivated, 0)
        a.recycle()

        colorActivated = ContextCompat.getColor(context, when {
            activeRes != 0 -> activeRes
            else -> R.color.colorPrimary
        })
        colorNotActivated = ContextCompat.getColor(context, when {
            deactiveRes != 0 -> deactiveRes
            else -> R.color.color_white_alpha_70
        })
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        when {
            activated -> setTextColor(colorActivated)
            else -> setTextColor(colorNotActivated)
        }
    }
}