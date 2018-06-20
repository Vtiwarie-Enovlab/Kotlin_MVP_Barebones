package com.enovlab.yoop.ui.transaction.ticket.details.adapter.vh

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem.AssigneeItem
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadUserImage
import kotlinx.android.synthetic.main.item_ticket_user.*

class TicketAssigneeViewHolder(parent: ViewGroup, val listener: (() -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_ticket_user, parent)) {

    fun bind(item: AssigneeItem) {
        ticket_page.text = "${item.page}"
        ticket_username.text = item.username
        ticket_section.text = item.sectionName

        ticket_create_id.setOnClickListener { listener?.invoke() }

        when (item) {
            is AssigneeItem.NoPhotoItem -> {
                ticket_image.setImageResource(R.drawable.ic_account_loop)
                ticket_status_transfer_caption.text = itemView.resources.getString(R.string.ticket_details_status_transfer_caption, item.assignerFirstName)
                ticket_image_verified.isVisible = false
                ticket_status_transfer.isVisible = true
                setupStatusEventReady(false)
                setupStatusVerified(false)
                ticket_create_id.isVisible = true
            }
            is AssigneeItem.EventReadyItem -> {
                ticket_image.loadUserImage(item.photoUrl)
                ticket_status_transfer_caption.text = itemView.resources.getString(R.string.ticket_details_status_transfer_caption, item.assignerFirstName)
                ticket_image_verified.isVisible = false
                ticket_status_transfer.isVisible = true
                setupStatusEventReady(true)
                setupStatusVerified(false)
                ticket_create_id.isInvisible = true
            }
            is AssigneeItem.VerifiedItem -> {
                ticket_image.loadUserImage(item.photoUrl)
                ticket_status_transfer_caption.text = itemView.resources.getString(R.string.ticket_details_status_transfer_caption, item.assignerFirstName)
                ticket_image_verified.isVisible = true
                ticket_status_transfer.isVisible = true
                setupStatusEventReady(true)
                setupStatusVerified(true)
                ticket_create_id.isInvisible = true
            }
        }
    }

    private fun setupStatusEventReady(active: Boolean) {
        val context = itemView.context
        if (active) {
            val padding = context.resources.getDimensionPixelSize(R.dimen.padding_ticket_status)
            val color = ContextCompat.getColor(context, R.color.color_white_alpha_50)

            ticket_status_ready_state.borderColor = Color.TRANSPARENT
            ticket_status_ready_state.borderWidth = 0
            ticket_status_ready_state.background = ContextCompat.getDrawable(context, R.drawable.oval_success)
            ticket_status_ready_state.setImageResource(R.drawable.ic_check_white_24dp)
            ticket_status_ready_state.updatePadding(padding, padding, padding, padding)

            ticket_status_ready_title.setTextColor(color)
            ticket_status_ready_caption.setTextColor(color)
        } else {
            val color = ContextCompat.getColor(context, R.color.color_white)

            ticket_status_ready_state.borderColor = color
            ticket_status_ready_state.borderWidth = context.resources.getDimensionPixelSize(R.dimen.margin_xxs)
            ticket_status_ready_state.background = null
            ticket_status_ready_state.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            ticket_status_ready_state.updatePadding(0, 0, 0, 0)

            ticket_status_ready_title.setTextColor(color)
            ticket_status_ready_caption.setTextColor(color)
        }
    }

    private fun setupStatusVerified(active: Boolean) {
        val context = itemView.context
        if (active) {
            val padding = context.resources.getDimensionPixelSize(R.dimen.padding_ticket_status)
            val color = ContextCompat.getColor(context, R.color.color_white_alpha_50)

            ticket_status_verified_state.borderColor = Color.TRANSPARENT
            ticket_status_verified_state.borderWidth = 0
            ticket_status_verified_state.background = ContextCompat.getDrawable(context, R.drawable.oval_accent)
            ticket_status_verified_state.setImageResource(R.drawable.ic_check_white_24dp)
            ticket_status_verified_state.updatePadding(padding, padding, padding, padding)

            ticket_status_verified_title.setTextColor(color)
            ticket_status_verified_caption.setTextColor(color)
        } else {
            val color = ContextCompat.getColor(context, R.color.color_white)

            ticket_status_verified_state.borderColor = color
            ticket_status_verified_state.borderWidth = context.resources.getDimensionPixelSize(R.dimen.margin_xxs)
            ticket_status_verified_state.background = null
            ticket_status_verified_state.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            ticket_status_verified_state.updatePadding(0, 0, 0, 0)

            ticket_status_verified_title.setTextColor(color)
            ticket_status_verified_caption.setTextColor(color)
        }
    }
}