package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.enovlab.yoop.R

/**
 * Created by m_toskhoparan on 03/05/18.
 */
class SpinnerLikeInputView : FrameLayout {

    private lateinit var inputView: InputView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val frameLayoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = frameLayoutParams

        val inputViewParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        inputView = InputView(context, attrs)
        inputView.layoutParams = inputViewParams
        inputView.getEditText().inputType = InputType.TYPE_NULL
//        inputView.getEditText().isEnabled = false
        inputView.getEditText().isFocusable = false
        inputView.getEditText().isFocusableInTouchMode = false
        inputView.getEditText().setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_arrow_down_white_12dp, null), null)
        inputView.setOnClickListener { performClick() }

        frameLayout.addView(inputView)

        addView(frameLayout)
    }

    fun setText(text: String) {
        inputView.setText(text)
    }
}