package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatButton
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.TextView
import com.enovlab.yoop.R
import com.enovlab.yoop.utils.TypefaceSpan

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
class PriceButton : AppCompatButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setPrice(price: Int, currency: String) {
        val from = resources.getString(R.string.event_landing_transaction_from)
        val signPrice = "$currency$price"
        val result = "$from $signPrice"

        val span = SpannableStringBuilder(result)

        val typefaceRegular = ResourcesCompat.getFont(context, R.font.roboto)!!
        span.setSpan(TypefaceSpan(typefaceRegular), 0, from.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val typefaceBold = ResourcesCompat.getFont(context, R.font.roboto_bold)!!
        span.setSpan(TypefaceSpan(typefaceBold), result.indexOf(signPrice), result.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        setText(span, TextView.BufferType.SPANNABLE)
    }
}