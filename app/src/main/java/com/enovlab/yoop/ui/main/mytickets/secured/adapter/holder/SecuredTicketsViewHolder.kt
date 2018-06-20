package com.enovlab.yoop.ui.main.mytickets.secured.adapter.holder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.SimpleItemAnimator
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import com.enovlab.yoop.ui.event.landing.adapter.TokenItemAdapter
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredItemDecoration
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import com.enovlab.yoop.utils.ext.loadUserImage
import kotlinx.android.synthetic.main.item_secured_ticket.*
import kotlinx.android.synthetic.main.item_token_assignment.*
import java.text.SimpleDateFormat
import java.util.*

class SecuredTicketsViewHolder(parent: ViewGroup,
                               private val tokenClickedListener: ((eventId: String, tokenItem: TokenItem) -> Unit)? = null,
                               private val eventListener: ((eventId: String) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_secured_ticket, parent)) {

    private val assignmentAdapter by lazy { TokenItemAdapter() }

    fun bind(securedToken: SecuredTokens.SecuredTokenItem) {
        showEventDetails(securedToken)
        showTokensList(securedToken)

        assignmentAdapter.listener = { tokenClickedListener?.invoke(securedToken.eventId, it) }
        secured_image.setOnClickListener { eventListener?.invoke(securedToken.eventId) }
    }

    private fun moreTicketsClicked(securedToken: SecuredTokens.SecuredTokenItem) {
        val items = securedToken.items

        txt_pending_assignment.isVisible = securedToken.pendingCount > 0
        txt_pending_assignment.text = itemView.resources.getQuantityString(R.plurals.my_tickets_secured_pending_asssignment, securedToken.pendingCount, securedToken.pendingCount)

        securedToken.expanded = !securedToken.expanded

        if (securedToken.expanded) {
            tokens_list.isVisible = true
            more_tickets.text = itemView.context.getString(R.string.my_tickets_secured_more_collapse)
            more_tickets.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black, 0)
            more_tickets.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_button_white_70)
        } else {
            tokens_list.isVisible = false
            more_tickets.text = itemView.resources.getQuantityString(R.plurals.my_tickets_secured, items.size, items.size)
            more_tickets.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_black, 0)
            more_tickets.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_button_white)
        }
    }

    private fun showEventDetails(securedToken: SecuredTokens.SecuredTokenItem) {
        secured_image.loadImage(securedToken.mediaUrl)

        secured_title.text = securedToken.eventName
        secured_date.text = DATE_FORMAT.format(securedToken.eventDate)
        secured_location.text = securedToken.locationName
    }

    private fun showTokensList(securedToken: SecuredTokens.SecuredTokenItem) {
        val context = itemView.context
        val items = securedToken.items

        //bind first item
        val firstItem = securedToken.firstItem
        bindFirstItem(securedToken.eventId, firstItem)

        //more tickets and bind remaining items
        if (items.size > 0) {
            //tokens list
            if (tokens_list.itemDecorationCount == 0) {
                tokens_list.addItemDecoration(SecuredItemDecoration(context.resources.getDimensionPixelSize(R.dimen.margin_small)))
            }
            (tokens_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            tokens_list.adapter = assignmentAdapter
            assignmentAdapter.submitList(items)
            showMoreTickets(true)
            more_tickets.text = context.resources.getQuantityString(R.plurals.my_tickets_secured, items.size, items.size)
            more_tickets.isVisible = true
            more_tickets.setOnClickListener {
                moreTicketsClicked(securedToken)
            }
        } else {
            showMoreTickets(false)
        }
    }

    private fun showMoreTickets(show: Boolean) {
        more_tickets.isVisible = show
    }

    private fun bindFirstItem(eventId: String, item: TokenItem) {
        val res = itemView.resources
        val context = itemView.context

        token_section_name.text = item.sectionName
        itemView.setOnClickListener { tokenClickedListener?.invoke(eventId, item) }

        when (item) {
            is TokenItem.UserNoPhotoTokenItem -> {
                token_status.setText(R.string.event_landing_token_user_photo)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_white))

                token_picture.setImageResource(R.drawable.ic_account_loop)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_on_sale_chance_wont)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is TokenItem.UserEventReadyTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.setText(R.string.event_landing_token_user_ready)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER_HORIZONTAL }

                token_event_ready.isVisible = true
                token_verified.isVisible = false
            }
            is TokenItem.UserVerifiedTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.setText(R.string.event_landing_token_user_verified)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = true
            }
            is TokenItem.UnassignedTokenItem -> {
                token_status.setText(R.string.event_landing_token_assign_required)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_wont))

                token_picture.setImageResource(R.drawable.oval_white_alpha_15)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_white_alpha_15)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is TokenItem.AssigneePendingTokenItem -> {
                token_status.text = res.getString(R.string.event_landing_token_assignee_pending, item.email)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_poor))

                token_picture.setImageResource(R.drawable.oval_white_alpha_15)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_white_alpha_15)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is TokenItem.AssigneeNoPhotoTokenItem -> {
                token_status.text = res.getString(R.string.event_landing_token_assignee_photo, item.firstName)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_white))

                token_picture.setImageResource(R.drawable.ic_account_loop)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_on_sale_chance_wont)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is TokenItem.AssigneeEventReadyTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.text = res.getString(R.string.event_landing_token_assignee_ready, item.firstName)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER_HORIZONTAL }

                token_event_ready.isVisible = true
                token_verified.isVisible = false
            }
            is TokenItem.AssigneeVerifiedTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.text = res.getString(R.string.event_landing_token_assignee_verified, item.firstName)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = true
            }
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("EEE, M/yy h a Z", Locale.getDefault())
    }

}