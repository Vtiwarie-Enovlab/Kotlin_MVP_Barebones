package com.enovlab.yoop.ui.transaction.ticket.details.adapter.vh

import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem.UnassignedItem
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_ticket_unassigned.*

class TicketUnassignedViewHolder(parent: ViewGroup, val listener: ((TicketItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_ticket_unassigned, parent)) {

    fun bind(item: UnassignedItem) {
        val context = itemView.context

        ticket_page.text = "${item.page}"
        ticket_username.text = context.getString(R.string.ticket_details_status_unassigned_number, item.page)
        ticket_section.text = item.sectionName

        ticket_transfer.setOnClickListener { listener?.invoke(item) }

        when (item) {
            is UnassignedItem.NoActionsItem -> {
                ticket_status_transfer_caption.setText(R.string.ticket_details_status_unassigned_caption)
                ticket_status_transfer_caption.setTextColor(ContextCompat.getColor(context, R.color.color_white))
            }
            is UnassignedItem.RevokedItem -> {
                ticket_status_transfer_caption.text = context.getString(R.string.ticket_details_status_unassigned_revoked, item.assigneeFirstName)
                ticket_status_transfer_caption.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_wont))
            }
            is UnassignedItem.ReturnedItem -> {
                ticket_status_transfer_caption.text = context.getString(R.string.ticket_details_status_unassigned_returned, item.assigneeFirstName)
                ticket_status_transfer_caption.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_wont))
            }
            is UnassignedItem.DeclinedItem -> {
                ticket_status_transfer_caption.text = context.getString(R.string.ticket_details_status_unassigned_declined, item.assigneeEmail)
                ticket_status_transfer_caption.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_wont))
            }
        }
    }
}