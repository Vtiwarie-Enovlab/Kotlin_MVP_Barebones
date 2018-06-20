package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.enovlab.yoop.R

/**
 * Created by m_toskhoparan on 03/05/18.
 */
class PasswordInputView : LinearLayout {

    private var colorDefault: Int = 0
    private var colorValid: Int = 0
    private var colorNotValid: Int = 0
    private var showHideState = false

    private lateinit var inputView: InputView
    private var validChars: TextView? = null
    private var validNums: TextView? = null

    private var isCharsValid: Boolean? = null
    private var isNumsValid: Boolean? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PasswordInputView, defStyleAttr, 0)

        val hasValidationFields = a.getBoolean(R.styleable.PasswordInputView_validationFields, false)

        a.recycle()

        init(hasValidationFields, attrs)
    }

    private fun init(hasValidationFields: Boolean, attrs: AttributeSet?) {
        colorDefault = ContextCompat.getColor(context, R.color.color_white)
        colorValid = ContextCompat.getColor(context, R.color.color_state_valid)
        colorNotValid = ContextCompat.getColor(context, R.color.color_input_error)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        val frameLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = frameLayoutParams

        val inputViewParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//        inputViewParams.topMargin = resources.getDimensionPixelSize(R.dimen.margin_large)
        inputView = InputView(context, attrs)
        inputView.layoutParams = inputViewParams

        val showHideParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        showHideParams.gravity = Gravity.END
//        showHideParams.topMargin = resources.getDimensionPixelSize(R.dimen.password_input_show_hide_margin)
        val showHide = TextView(context)
        showHide.setAllCaps(true)
        showHide.layoutParams = showHideParams
        showHide.setAllCaps(true)
        showHide.setText(R.string.password_input_show)
        showHide.setTextColor(colorDefault)
        showHide.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_small))
        showHide.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
//        showHide.background = getSelectableDrawable()
        showHide.setOnClickListener {
            showHideState = !showHideState
            showHide.setText(if (showHideState) R.string.password_input_hide else R.string.password_input_show)
            inputView.transformationMethod(if (showHideState) null else PasswordTransformationMethod())
        }

        frameLayout.addView(showHide)
        frameLayout.addView(inputView)

        if (hasValidationFields) {
            val validCharsParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            validCharsParams.topMargin = resources.getDimensionPixelSize(R.dimen.margin_small)
            validChars = TextView(context)
            validChars!!.setText(R.string.password_input_valid_chars)
            validChars!!.layoutParams = validCharsParams
            validChars!!.setTextColor(colorDefault)
            validChars!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_small))
            validChars!!.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)

            val validNumsParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            validNums = TextView(context)
            validNums!!.setText(R.string.password_input_valid_nums)
            validNums!!.layoutParams = validNumsParams
            validNums!!.setTextColor(colorDefault)
            validNums!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_small))
            validNums!!.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        }

        addView(frameLayout)
        if (hasValidationFields) {
            addView(validChars)
            addView(validNums)

            textChangeListener {
                if (isCharsValid == false) resetMinCharactersValidation()
                if (isNumsValid == false) resetDigitsValidation()
            }
        }
    }

    fun minCharactersValid(valid: Boolean) {
        isCharsValid = valid
        validChars?.setTextColor(if (valid) colorValid else colorDefault)
    }

    fun digitsValid(valid: Boolean) {
        isNumsValid = valid
        validNums?.setTextColor(if (valid) colorValid else colorDefault)
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

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        inputView.onFocusChangeListener = l
    }

    private fun resetMinCharactersValidation() {
        isCharsValid = null
        validChars?.setTextColor(colorDefault)
    }

    private fun resetDigitsValidation() {
        isNumsValid = null
        validNums?.setTextColor(colorDefault)
    }

//
//    private fun getSelectableDrawable(): Drawable {
//        val attrs = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
//        val ta = context.obtainStyledAttributes(attrs)
//        val drawableFromTheme = ta.getDrawable(0)
//        ta.recycle()
//        return drawableFromTheme
//    }
}