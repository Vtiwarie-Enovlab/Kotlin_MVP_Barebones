package com.enovlab.yoop.ui.widget

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.utils.ext.delayedTextChangeListener
import com.enovlab.yoop.utils.ext.textChangeListener

class ChancesInputView : RelativeLayout {

    var focusListener: ((Boolean) -> Unit)? = null

    private lateinit var sign: TextView
    private lateinit var input: DefaultHintEditText
    private lateinit var underline: View
    private lateinit var caption: TextView

    private var colorDefault: Int = 0
    private var colorWhite: Int = 0
    private var colorNegligible: Int = 0
    private var colorPoor: Int = 0
    private var colorOk: Int = 0
    private var colorGood: Int = 0
    private var colorGreat: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        colorDefault = ContextCompat.getColor(context, R.color.color_hint_transactions)
        colorWhite = ContextCompat.getColor(context, R.color.color_white)
        colorNegligible = ContextCompat.getColor(context, R.color.color_on_sale_chance_wont)
        colorPoor = ContextCompat.getColor(context, R.color.color_on_sale_chance_poor)
        colorOk = ContextCompat.getColor(context, R.color.color_on_sale_chance_ok)
        colorGood = ContextCompat.getColor(context, R.color.color_on_sale_chance_good)
        colorGreat = ContextCompat.getColor(context, R.color.color_on_sale_chance_great)

        val containerInputParams = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        containerInputParams.addRule(CENTER_HORIZONTAL)

        val containerInput = LinearLayout(context)
        containerInput.layoutParams = containerInputParams
        containerInput.orientation = LinearLayout.HORIZONTAL
        containerInput.gravity = Gravity.CENTER
        containerInput.id = View.generateViewId()

        val signParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        signParams.marginEnd = resources.getDimensionPixelSize(R.dimen.margin_extra_small)

        sign = TextView(context)
        sign.layoutParams = signParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sign.setTextAppearance(R.style.Text_Medium_Headline_White)
        } else {
            sign.setTextAppearance(context, R.style.Text_Medium_Headline_White)
        }

        val inputParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        inputParams.topMargin = -(resources.getDimensionPixelSize(R.dimen.margin_medium))
        inputParams.bottomMargin = -(resources.getDimensionPixelSize(R.dimen.margin_medium))

        input = DefaultHintEditText(context)
        input.layoutParams = inputParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            input.setTextAppearance(R.style.Text_Bold)
        } else {
            input.setTextAppearance(context, R.style.Text_Bold)
        }
        input.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        input.keyListener = DigitsKeyListener.getInstance("0123456789")
        input.isFocusable = true
        input.isFocusableInTouchMode = true
        input.gravity = Gravity.CENTER
        input.includeFontPadding = false
        input.inputType = EditorInfo.TYPE_CLASS_NUMBER
        input.setLineSpacing(0F, 0F)
        input.minWidth = resources.getDimensionPixelSize(R.dimen.chances_input_view_max_width)
        input.setSelectAllOnFocus(false)
        input.setSingleLine(true)
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70F)
        input.updatePaddingRelative(bottom = 0, top = 0, end = resources.getDimensionPixelSize(R.dimen.padding_small))
        input.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))

        containerInput.addView(sign)
        containerInput.addView(input)

        val underlineParams = RelativeLayout.LayoutParams(WRAP_CONTENT, resources.getDimensionPixelSize(R.dimen.padding_extra_small))
        underlineParams.topMargin = resources.getDimensionPixelSize(R.dimen.margin_small)
        underlineParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_small)
        underlineParams.addRule(BELOW, containerInput.id)
        underlineParams.addRule(ALIGN_START, containerInput.id)
        underlineParams.addRule(ALIGN_END, containerInput.id)
        underlineParams.addRule(CENTER_HORIZONTAL)

        underline = View(context)
        underline.layoutParams = underlineParams
        underline.id = View.generateViewId()

        val captionParams = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        captionParams.addRule(BELOW, underline.id)
        captionParams.addRule(CENTER_HORIZONTAL)

        caption = TextView(context)
        caption.layoutParams = captionParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            caption.setTextAppearance(R.style.Text_Bold_Medium)
        } else {
            caption.setTextAppearance(context, R.style.Text_Bold_Medium)
        }

        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        addView(containerInput)
        addView(underline)
        addView(caption)

        defaultState()

        input.setOnFocusChangeListener { _, hasFocus -> focusListener?.invoke(hasFocus) }
    }

    fun setText(text: String) {
        input.setText(text)
    }

    fun setHint(hint: String) {
        input.hint = hint
    }

    fun setCurrency(currency: String?) {
        sign.text = currency
    }

    fun defaultState() {
        sign.setTextColor(colorWhite)
        input.setTextColor(colorDefault)
        underline.setBackgroundColor(colorWhite)

        caption.text = ""
    }

    fun wontChance(minAskPrice: Int) {
        sign.setTextColor(colorNegligible)
        input.setTextColor(colorNegligible)
        underline.setBackgroundColor(colorNegligible)
        caption.setTextColor(colorNegligible)

        caption.text = resources.getString(R.string.transaction_count_chances_wont, sign.text, minAskPrice.toString())
    }

    fun negligibleChance() {
        sign.setTextColor(colorNegligible)
        input.setTextColor(colorNegligible)
        underline.setBackgroundColor(colorNegligible)
        caption.setTextColor(colorNegligible)

        caption.setText(R.string.transaction_count_chances_negligible)
    }

    fun chance(chance: Chance) {
        var color = 0
        var chanceText = ""

        when (chance) {
            Chance.GREAT -> {
                color = colorGreat
                chanceText = resources.getString(R.string.transaction_count_chances_great)
            }
            Chance.GOOD -> {
                color = colorGood
                chanceText = resources.getString(R.string.transaction_count_chances_good)
            }
            Chance.LOW -> {
                color = colorOk
                chanceText = resources.getString(R.string.transaction_count_chances_ok)
            }
            Chance.POOR -> {
                color = colorPoor
                chanceText = resources.getString(R.string.transaction_count_chances_poor)
            }
        }

        sign.setTextColor(color)
        input.setTextColor(color)
        underline.setBackgroundColor(color)
        caption.setTextColor(color)

        caption.text = resources.getString(R.string.transaction_count_chances, chanceText)
    }

    fun textDelayedChangeListener(listener: (String) -> Unit, delay: Long = 350L) {
        input.delayedTextChangeListener(listener, delay)
    }

    fun textChangeListener(listener: (String) -> Unit) {
        input.textChangeListener(listener)
    }
}