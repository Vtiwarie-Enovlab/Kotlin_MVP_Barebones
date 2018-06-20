package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import com.enovlab.yoop.R

class CardInputView : FrameLayout {

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
        inputView = createInputView(attrs)
        inputView.layoutParams = inputViewParams
        inputView.getEditText().apply {
            setTextIsSelectable(false)
        }
        frameLayout.addView(inputView)
        addView(frameLayout)
    }

    protected fun <T: InputView> createInputView(attrs: AttributeSet?): T {
        return object: InputView(context, attrs)  {
            override fun <V : TextInputEditText> createEditText(): V {
                return NoSelectionTextInputEditText(context) as V
            }
        } as T
    }

    fun showCard(card: CARD?) {
        val drawables = inputView.getEditText().compoundDrawables
        val cardDrawable = if (card?.id != null) resources.getDrawable(card?.id, null) else null
        inputView.getEditText().setCompoundDrawablesWithIntrinsicBounds(cardDrawable, drawables[1], drawables[2], drawables[3])
    }

    fun isValid(valid: Boolean) {
        inputView.isValid(valid)
    }

    fun textChangeListener(listener: (String) -> Unit/*, onChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null*/) {
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

    override fun setOnClickListener(l: OnClickListener) {
        inputView.setOnClickListener(l)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        inputView.onFocusChangeListener = l
    }

    //TODO create more cards
    enum class CARD(@DrawableRes val id: Int) {
        VISA(R.drawable.icon_payment_card_visa),
        MASTERCARD(R.drawable.icon_payment_card_mc),
        MAESTRO(R.drawable.icon_payment_card_mc)
    }
}