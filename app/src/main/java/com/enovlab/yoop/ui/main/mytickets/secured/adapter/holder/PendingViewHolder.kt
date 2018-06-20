package com.enovlab.yoop.ui.main.mytickets.secured.adapter.holder

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder.RequestedBaseViewHolder
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_secured_ticket_pending.*
import java.text.SimpleDateFormat
import java.util.*

class PendingViewHolder(parent: ViewGroup,
                        private val eventListener: ((eventId: String) -> Unit)? = null)
    : RequestedBaseViewHolder(inflateView(R.layout.item_secured_ticket_pending, parent)) {

    fun bind(pendingToken: SecuredTokens.PendingTokenItem) {
        showEventDetails(pendingToken)

        txt_pending_assignment.text = itemView.resources.getQuantityString(R.plurals.my_tickets_secured_pending_asssignment, pendingToken.pendingCount, pendingToken.pendingCount)
        itemView.setOnClickListener { eventListener?.invoke(pendingToken.eventId) }
    }

    private fun showEventDetails(securedToken: SecuredTokens.PendingTokenItem) {
        secured_image.loadImage(securedToken.mediaUrl)

        secured_title.text = securedToken.eventName
        secured_date.text = DATE_FORMAT.format(securedToken.eventDate)
        secured_location.text = securedToken.locationName
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("EEE, M/yy h a Z", Locale.getDefault())
    }
}