package com.enovlab.yoop.ui.event.landing.adapter

import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem.*
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadUserImage
import kotlinx.android.synthetic.main.item_token_assignment.*

class TokenItemViewHolder(parent: ViewGroup, val listener: ((TokenItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_token_assignment, parent)) {

    fun bind(item: TokenItem) {
        val res = itemView.resources
        val context = itemView.context

        token_section_name.text = item.sectionName
        itemView.setOnClickListener { listener?.invoke(item) }

        when (item) {
            is UserNoPhotoTokenItem -> {
                token_status.setText(R.string.event_landing_token_user_photo)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_white))

                token_picture.setImageResource(R.drawable.ic_account_loop)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_on_sale_chance_wont)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is UserEventReadyTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.setText(R.string.event_landing_token_user_ready)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER_HORIZONTAL }

                token_event_ready.isVisible = true
                token_verified.isVisible = false
            }
            is UserVerifiedTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.setText(R.string.event_landing_token_user_verified)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = true
            }
            is UnassignedTokenItem -> {
                token_status.setText(R.string.event_landing_token_assign_required)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_wont))

                token_picture.setImageResource(R.drawable.oval_white_alpha_15)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_white_alpha_15)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is AssigneePendingTokenItem -> {
                token_status.text = res.getString(R.string.event_landing_token_assignee_pending, item.email)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_on_sale_chance_poor))

                token_picture.setImageResource(R.drawable.oval_white_alpha_15)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_white_alpha_15)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is AssigneeNoPhotoTokenItem -> {
                token_status.text = res.getString(R.string.event_landing_token_assignee_photo, item.firstName)
                token_status.setTextColor(ContextCompat.getColor(context, R.color.color_white))

                token_picture.setImageResource(R.drawable.ic_account_loop)
                token_picture.borderColor = ContextCompat.getColor(context, R.color.color_on_sale_chance_wont)
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER }

                token_event_ready.isInvisible = true
                token_verified.isVisible = false
            }
            is AssigneeEventReadyTokenItem -> {
                val color = ContextCompat.getColor(context, R.color.color_white)

                token_status.text = res.getString(R.string.event_landing_token_assignee_ready, item.firstName)
                token_status.setTextColor(color)

                token_picture.loadUserImage(item.photoUrl)
                token_picture.borderColor = color
                token_picture.updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.CENTER_HORIZONTAL }

                token_event_ready.isVisible = true
                token_verified.isVisible = false
            }
            is AssigneeVerifiedTokenItem -> {
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
}