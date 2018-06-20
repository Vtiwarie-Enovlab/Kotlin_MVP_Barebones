package com.enovlab.yoop.ui.payments.add.billing.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.Country
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_country.*

/**
 * @author vishaan
 */
class CountriesViewHolder(parent: ViewGroup, val listener: ((Country) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_country, parent)) {

    fun bind(item: Country) {
        country.text = item.name
        flag.text = localeToEmoji(item.code)
        if (listener != null)
            itemView.setOnClickListener { listener.invoke(item) }
    }

    companion object {
        private fun localeToEmoji(countryCode: String): String {
            val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter) + Character.toChars(secondLetter))
        }
    }
}