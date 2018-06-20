package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import com.enovlab.yoop.R
import timber.log.Timber

/**
 * Created by m_toskhoparan on 03/05/18.
 */
class CVVInputView : FrameLayout {

    private var colorDefault: Int = 0
    private var colorValid: Int = 0
    private var colorNotValid: Int = 0

    private lateinit var inputView: InputView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PasswordInputView, defStyleAttr, 0)

        a.recycle()

        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        colorDefault = ContextCompat.getColor(context, R.color.color_white)
        colorValid = ContextCompat.getColor(context, R.color.color_state_valid)
        colorNotValid = ContextCompat.getColor(context, R.color.color_input_error)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val frameLayoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = frameLayoutParams

        val inputViewParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        inputView = InputView(context, attrs)
        inputView.layoutParams = inputViewParams
        frameLayout.addView(inputView)

        addView(frameLayout)
    }

    fun isValid(valid: Boolean) {
        inputView.isValid(valid)
    }

    fun textChangeListener(listener: (String) -> Unit) {
        inputView.textChangeListener(listener::invoke)
    }

    fun errorEnabled(enabled: Boolean) {
        inputView.errorEnabled(enabled)
    }

    override fun setEnabled(enabled: Boolean) {
        inputView.isEnabled = enabled
    }

    override fun clearFocus() {
        inputView.clearFocus()
    }

    fun focus() {
        inputView.focus()
    }

    fun getText(): String {
        return inputView.getText()
    }

    fun setText(text: String) {
        inputView.setText(text)
    }

    fun getInputView(): InputView {
        return inputView
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        inputView.onFocusChangeListener = l
    }
}