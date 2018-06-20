package com.enovlab.yoop.ui.payments.manage.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView

class AddNewCardViewHolder(parent: ViewGroup, val listener: (() -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_manage_payments_add_new_card, parent)) {

    fun bind() {
        if (listener != null) {
            itemView.setOnClickListener { listener?.invoke() }
        }
    }
}