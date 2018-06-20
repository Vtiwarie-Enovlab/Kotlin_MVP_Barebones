package com.enovlab.yoop.ui.transaction.ticket.details.adapter.vh

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_ticket_pending.*

class TicketPendingViewHolder(parent: ViewGroup)
    : BaseViewHolder(inflateView(R.layout.item_ticket_pending, parent)) {

    fun bind(item: TicketItem.PendingItem) {
        ticket_page.text = "${item.page}"
        ticket_username.text = item.email
        ticket_section.text = item.sectionName
        ticket_status_transfer_caption.text = itemView.resources.getString(R.string.ticket_details_status_pending_sent, item.email, item.assignmentDate)
    }
}