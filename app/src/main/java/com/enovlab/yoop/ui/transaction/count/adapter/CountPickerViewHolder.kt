package com.enovlab.yoop.ui.transaction.count.adapter

import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import android.widget.TextView
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView

/**
 * Created by mtosk on 3/8/2018.
 */
class CountPickerViewHolder(parent: ViewGroup, val listener: ((CountItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_transaction_count_picker, parent)) {

    fun bind(item: CountItem) {
        val colorTextDefault = ContextCompat.getColor(itemView.context, R.color.color_white_alpha_70)
        val colorTextSelected = ContextCompat.getColor(itemView.context, R.color.colorPrimary)

        (containerView as TextView).apply {
            text = item.count.toString()

            setTextColor(if (item.selected) colorTextSelected else colorTextDefault)
            isActivated = item.selected
        }

        if (listener != null)
            containerView.setOnClickListener { listener.invoke(item) }
    }
}