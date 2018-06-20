package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout

class InsetsNestedScrollView : NestedScrollView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return super.onApplyWindowInsets(
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom))
    }
}