package com.enovlab.yoop.ui.main.mytickets.secured.adapter.holder

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder.RequestedBaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_secured_unverified_ticket.*

class UnVerifiedViewHolder(parent: ViewGroup,
                           private val createIdListener: (() -> Unit)? = null)
    : RequestedBaseViewHolder(inflateView(R.layout.item_secured_unverified_ticket, parent)) {

    fun bind() {
        create_id.setOnClickListener { createIdListener?.invoke() }
    }
}