package com.enovlab.yoop.ui.main.discover.adapter

import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance.*
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_discover_list.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mtosk on 3/8/2018.
 */

class DiscoverViewHolder(parent: ViewGroup, val listener: ((DiscoverItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_discover_list, parent)) {

    fun bind(discoverItem: DiscoverItem) {
        val url = discoverItem.event.defaultMedia?.url
        when {
            url != null -> image.loadImage(url)
            else -> image.setImageDrawable(null)
        }

        title.text = discoverItem.event.shortName

        location.text = when {
            discoverItem.event.date == null && discoverItem.event.locationName == null -> null
            else -> "${DATE_FORMAT.format(discoverItem.event.date)} • ${discoverItem.event.locationName}"
        }

        setupPills(discoverItem.pills)

        if (listener != null)
            containerView?.setOnClickListener { listener.invoke(discoverItem) }
    }

    private fun setupPills(pills: List<DiscoverItem.Pill>) {
        pill_new.isVisible = false
        pill_normal.isVisible = false
        pill_going.isVisible = false
        pill_active.isVisible = false

        pills.forEach { pill ->
            when (pill) {
                is DiscoverItem.Pill.NewPill -> {
                    pill_new.isVisible = true
                }
                is DiscoverItem.Pill.NormalListPill -> {
                    pill_normal.isVisible = true
                    if (pill.price != null) {
                        val multiple = if (pill.multiple) "+" else ""
                        pill_normal_price.text = "${pill.currency}${pill.price!!.toInt()}$multiple"
                    }
                    pill_normal_icon.setImageResource(R.drawable.ic_list_black)
                }
                is DiscoverItem.Pill.NormalOnSalePill -> {
                    pill_normal.isVisible = true
                    if (pill.price != null) {
                        val multiple = if (pill.multiple) "+" else ""
                        pill_normal_price.text = "${pill.currency}${pill.price!!.toInt()}$multiple"
                    }
                    pill_normal_icon.setImageResource(R.drawable.ic_onsale_black)
                }
                is DiscoverItem.Pill.GoingPill -> {
                    pill_going.isVisible = true
                    pill_going_user_picture.loadImage(pill.userPhoto)
                }
                is DiscoverItem.Pill.ActiveListPill -> {
                    pill_active.isVisible = true
                    pill_active_user_picture.loadImage(pill.userPhoto)
                    pill_active_transaction_icon.setImageResource(R.drawable.ic_list_black)
                    pill_active_user_picture.borderColor = ContextCompat.getColor(itemView.context, R.color.colorAccent)
                }
                is DiscoverItem.Pill.ActiveOnSalePill -> {
                    pill_active.isVisible = true
                    pill_active_user_picture.loadImage(pill.userPhoto)
                    pill_active_transaction_icon.setImageResource(R.drawable.ic_onsale_black)
                    pill_active_user_picture.borderColor = when(pill.chance) {
                        GREAT -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_great)
                        GOOD -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_good)
                        LOW -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_ok)
                        POOR -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_poor)
                        else -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_wont)
                    }
                }
            }
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("EEE, M/dd", Locale.getDefault())
    }
}