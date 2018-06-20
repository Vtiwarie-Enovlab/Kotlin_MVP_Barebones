package com.enovlab.yoop.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.enovlab.yoop.R

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
class ArrowFilterStateButton : FilterStateButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        when {
            activated -> setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_dark_24dp,0)
            else -> setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_white_opacity_70_24dp,0)
        }
    }
}