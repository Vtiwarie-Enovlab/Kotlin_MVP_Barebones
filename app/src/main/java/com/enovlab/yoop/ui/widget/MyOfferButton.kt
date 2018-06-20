package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.TextView
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.utils.TypefaceSpan

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
class MyOfferButton : MyRequestsButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setPrice(price: Int, currency: String) {
        val myOffer = resources.getString(R.string.event_landing_transaction_my_offer)
        val signPrice = "$currency$price"
        val result = "$myOffer $signPrice"

        val span = SpannableStringBuilder(result)

        val typefaceRegular = ResourcesCompat.getFont(context, R.font.roboto)!!
        span.setSpan(TypefaceSpan(typefaceRegular), 0, myOffer.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val typefaceBold = ResourcesCompat.getFont(context, R.font.roboto_bold)!!
        span.setSpan(TypefaceSpan(typefaceBold), result.indexOf(signPrice), result.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        setText(span, TextView.BufferType.SPANNABLE)
    }

    fun chance(chance: Chance?) {
        background = ContextCompat.getDrawable(context, when (chance) {
            Chance.GREAT -> R.drawable.background_button_chances_great
            Chance.GOOD -> R.drawable.background_button_chances_good
            Chance.LOW -> R.drawable.background_button_chances_ok
            Chance.POOR -> R.drawable.background_button_chances_poor
            else -> R.drawable.background_button_chances_negligible
        })
    }
}