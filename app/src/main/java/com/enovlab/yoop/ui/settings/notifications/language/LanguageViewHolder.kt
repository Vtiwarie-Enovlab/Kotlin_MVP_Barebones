package com.enovlab.yoop.ui.settings.notifications.language

import android.view.ViewGroup
import android.widget.TextView
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView

class LanguageViewHolder(parent: ViewGroup, val listener: ((LanguageItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_settings_language, parent)) {

    fun bind(item: LanguageItem) {
        (itemView as TextView).text = item.locale.displayLanguage

        if (listener != null) {
            itemView.setOnClickListener { listener.invoke(item) }
        }
    }
}