package com.enovlab.yoop.ui.widget

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.enovlab.yoop.R
import com.enovlab.yoop.utils.ext.asText
import com.enovlab.yoop.utils.ext.postView
import com.enovlab.yoop.utils.ext.showKeyboard
import com.enovlab.yoop.utils.ext.textChangeListener
import timber.log.Timber

/**
 * Created by mtosk on 3/5/2018.
 */
open class InputView : LinearLayout {

    private var colorDefault: Int = 0
    private var colorActive: Int = 0
    private var colorError: Int = 0
    private lateinit var underlineDrawable: Drawable

    private lateinit var inputLayout: TextInputLayout
    private lateinit var editText: TextInputEditText
    private lateinit var underline: View

    private var errorEnabled = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.InputView, defStyleAttr, 0)

        val hintRes = a.getResourceId(R.styleable.InputView_android_hint, 0)
        val imeOptions = a.getInt(R.styleable.InputView_android_imeOptions, 0)
        val inputType = a.getInt(R.styleable.InputView_android_inputType, 0)
        val length = a.getInt(R.styleable.InputView_android_maxLength, 100)
        val digits = when {
            a.hasValue(R.styleable.InputView_android_digits) -> a.getString(R.styleable.InputView_android_digits)
            else -> ""
        }
        val textSize = a.getDimension(R.styleable.InputView_android_textSize, 0f)
        val editable = a.getBoolean(R.styleable.InputView_editable, true)
        val drawableEnd = a.getResourceId(R.styleable.InputView_android_drawableEnd, 0)

        a.recycle()

        init(hintRes, imeOptions, inputType, length, digits, textSize, editable, drawableEnd)
    }

    private fun init(hintRes: Int, imeOptions: Int, inputType: Int, length: Int,
                     digits: String, textSize: Float, editable: Boolean,
                     @DrawableRes drawableEnd: Int) {

        colorDefault = ContextCompat.getColor(context, R.color.color_white_alpha_50)
        colorActive = ContextCompat.getColor(context, R.color.color_white)
        colorError = ContextCompat.getColor(context, R.color.color_input_error)
        underlineDrawable = ContextCompat.getDrawable(context, R.drawable.background_input_view_underline)!!

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        val defaultTextSize = resources.getDimension(R.dimen.text_size_large)
        var defaultInputHeight = resources.getDimensionPixelSize(R.dimen.text_input_edit_text_size)
        var defaultMargin = resources.getDimensionPixelSize(R.dimen.margin_small)
        if (textSize > 0) {
            val delta = textSize / defaultTextSize
            defaultInputHeight = (defaultInputHeight.toFloat() * delta).toInt()
            defaultMargin = (defaultMargin.toFloat() * delta).toInt()
        }

        val inputLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        inputLayoutParams.bottomMargin = defaultMargin
        inputLayout = TextInputLayout(context)
        inputLayout.layoutParams = inputLayoutParams

        val editTextParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, defaultInputHeight)
//        val editTextParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        editText = createEditText()
        editText.layoutParams = editTextParams
        editText.setPaddingRelative(0, resources.getDimensionPixelSize(R.dimen.padding_extra_small), 0, 0)
        editText.imeOptions = imeOptions
        editText.inputType = inputType
        editText.hint = resources.getString(hintRes)
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.setTextColor(if (editable) colorActive else colorDefault)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, if (textSize != 0f) textSize else defaultTextSize)
        editText.maxLines = 1
        editText.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        editText.background = null
        if(length >= 0) editText.filters = arrayOf(InputFilter.LengthFilter(length))
        if(digits.isNotEmpty()) editText.keyListener = DigitsKeyListener.getInstance(digits)
        editText.setOnFocusChangeListener { _, hasFocus ->
            underline.isActivated = hasFocus
        }
        editText.compoundDrawablePadding = 0

        if (drawableEnd != 0) {
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableEnd, 0)
        }

        if (!editable) {
            inputLayout.isEnabled = false
            editText.isEnabled = false
            editText.isFocusable = false
        }

        inputLayout.addView(editText)

        underline = View(context)
        underline.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.underline_size))
        underline.background

        addView(inputLayout)
        addView(underline)

        errorEnabled(false)

        textChangeListener ({
            if (errorEnabled || it.isEmpty()) errorEnabled(false)
        })
    }

    protected open fun <T: TextInputEditText> createEditText(): T {
        return TextInputEditText(context, null) as T
    }

    fun errorEnabled(enabled: Boolean) {
        errorEnabled = enabled
        when {
            enabled && hasText() -> {
                inputLayout.setHintTextAppearance(R.style.TextInputLayoutHint_Error)
                updateHintColor(colorError)
                underline.setBackgroundColor(colorError)
            }
            else -> {
                inputLayout.setHintTextAppearance(R.style.TextInputLayoutHint_Default)
                updateHintColor(colorDefault)
                underline.background = underlineDrawable
            }
        }
    }

    fun isValid(valid: Boolean) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            if (valid) R.drawable.ic_done_success_24dp else 0, 0)
    }

    fun transformationMethod(method: TransformationMethod? = null) {
        editText.transformationMethod = method
        editText.setSelection(editText.text.length)
    }

    fun textChangeListener(listener: (String) -> Unit) {
        editText.textChangeListener(listener::invoke)
    }

    fun setText(text: String?) {
        editText.setText(text)
        editText.setSelection(text?.length ?: 0)
    }

    fun getText() = editText.asText()

    fun setHint(@StringRes id: Int) {
        inputLayout.hint = resources.getString(id)
    }

    override fun setEnabled(enabled: Boolean) {
        editText.isEnabled = enabled
    }

    override fun clearFocus() {
        editText.clearFocus()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        editText.setOnClickListener(l)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText.onFocusChangeListener = l
    }

    fun focus() {
        clearFocus()
        editText.requestFocus()
        editText.postView { it.showKeyboard() }
    }

    private fun hasText(): Boolean = editText.text.isNotEmpty()


    fun getEditText(): EditText {
        return editText
    }

    private fun updateHintColor(color: Int) {
        try {
            val field = inputLayout.javaClass.getDeclaredField("mDefaultTextColor")
            field.isAccessible = true

            val colorStateList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))
            field.set(inputLayout, colorStateList)

            val method = inputLayout.javaClass.getDeclaredMethod("updateLabelState", Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(inputLayout, true)
        } catch (e: Exception) {
            Timber.e(e, "Unable to set hint color via reflection")
        }
    }
}