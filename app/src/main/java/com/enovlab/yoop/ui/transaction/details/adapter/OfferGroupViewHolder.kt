package com.enovlab.yoop.ui.transaction.details.adapter

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.Demand
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_transaction_details_offer_group.*

/**
 * Created by mtosk on 3/8/2018.
 */
class OfferGroupViewHolder(parent: ViewGroup, val listener: ((OfferGroupItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_transaction_details_offer_group, parent)) {

    fun bind(offerGroup: OfferGroupItem) {
        offer_group_min_offer_title.isVisible = false
        offer_group_min_offer.isVisible = false
        offer_group_demand.isVisible = false
        offer_group_user_tickets.isVisible = false
        offer_group_picture.borderColor = Color.TRANSPARENT
        offer_group_picture.setImageDrawable(null)

        when {
            offerGroup is OfferGroupItem.ListOfferGroup -> {
                offer_group_admission.text = offerGroup.description
                offer_group_offer_title.setText(R.string.transaction_details_ticket_available)
                offer_group_offer.text = offerGroup.ticketCount.toString()
                offer_group_price.text = "${offerGroup.currency}${offerGroup.listPrice}"

                when {
                    offerGroup.hasUserActivity -> {
                        offer_group_picture.borderColor = ContextCompat.getColor(itemView.context, R.color.colorAccent)

                        when {
                            offerGroup.userPhoto != null -> offer_group_picture.loadImage(offerGroup.userPhoto)
                            else -> offer_group_picture.setImageResource(R.drawable.ic_account_loop)
                        }
                    }
                    else -> offer_group_picture.setImageResource(R.drawable.ic_arrow_right_accent_24dp)
                }

                if (offerGroup.userTickets > 0) {
                    offer_group_user_tickets.isVisible = true
                    offer_group_user_tickets.text = itemView.resources.getQuantityString(R.plurals.transaction_details_user_tickets, offerGroup.userTickets, offerGroup.userTickets)
                }
            }
            offerGroup is OfferGroupItem.OnSaleOfferGroup -> {
                offer_group_admission.text = offerGroup.description
                offer_group_offer_title.setText(R.string.transaction_details_avg_offer)
                offer_group_offer.text = "${offerGroup.currency}${offerGroup.averageOfferPrice}"
                offer_group_price.text = "${offerGroup.currency}${offerGroup.minAskPrice}"

                when (offerGroup.demand) {
                    Demand.HIGH -> {
                        offer_group_demand.isVisible = true
                        offer_group_demand.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_poor))
                        offer_group_demand.setText(R.string.transaction_count_demand_high)
                    }
                    Demand.DEMAND_EXCEEDS_SUPPLY -> {
                        offer_group_demand.isVisible = true
                        offer_group_demand.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_wont))
                        offer_group_demand.setText(R.string.transaction_count_demand_exceeds_supply)

                        offer_group_min_offer_title.isVisible = true
                        offer_group_min_offer.isVisible = true

                        offer_group_min_offer.text = "${offerGroup.currency}${offerGroup.minOfferPrice}  •  "
                    }
                    else -> offer_group_demand.isVisible = false
                }

                when {
                    offerGroup.hasUserActivity -> {
                        offer_group_picture.borderColor = when (offerGroup.chance) {
                            Chance.GREAT -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_great)
                            Chance.GOOD -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_good)
                            Chance.LOW -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_ok)
                            Chance.POOR -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_poor)
                            else -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_wont)
                        }

                        when {
                            offerGroup.userPhoto != null -> offer_group_picture.loadImage(offerGroup.userPhoto)
                            else -> offer_group_picture.setImageResource(R.drawable.ic_account_loop)
                        }
                    }
                    else -> {
                        offer_group_picture.background = null
                        offer_group_picture.setImageResource(R.drawable.ic_arrow_right_accent_24dp)
                        offer_group_picture.borderColor = ContextCompat.getColor(itemView.context, android.R.color.transparent)
                    }
                }

                if (offerGroup.userTickets > 0) {
                    offer_group_user_tickets.isVisible = true
                    offer_group_user_tickets.text = itemView.resources.getQuantityString(R.plurals.transaction_details_user_tickets, offerGroup.userTickets, offerGroup.userTickets)
                }
            }
        }

        if (listener != null)
            containerView?.setOnClickListener { listener.invoke(offerGroup) }
    }
}