package com.enovlab.yoop.ui.transaction.ticket.transfer.contacts

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.Contact
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_ticket_details_contact.*

class ContactsViewHolder(parent: ViewGroup, val listener: ((Contact) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_ticket_details_contact, parent)) {

    fun bind(item: Contact) {
        contact_name.text = item.name
        contact_email.text = item.email
        contact_email.isVisible = item.email != null && item.email!!.isNotBlank()

        itemView.setOnClickListener { listener?.invoke(item) }
    }
}