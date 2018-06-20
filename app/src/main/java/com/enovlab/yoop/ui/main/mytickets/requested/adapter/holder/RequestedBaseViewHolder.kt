package com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder

import android.support.v7.util.DiffUtil
import android.support.v7.widget.SimpleItemAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseAdapter
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem
import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem.*
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersItemDecoration
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_lost.*
import kotlinx.android.synthetic.main.layout_item_requested_lost.*

abstract class RequestedBaseViewHolder(container: View) : BaseViewHolder(container) {

    protected fun bindLostOffers(lostOffers: List<TransactionOfferItem>) {

        val lostOnSaleOffers = lostOffers.filter { it is TransactionOfferItem.LostOnSaleOfferItem }
        if (lostOnSaleOffers.isNotEmpty()) {
            container_lost_on_sale.isVisible = true

            val size = lostOnSaleOffers.size
            requested_lost_on_sale_header.text = itemView.resources.getQuantityString(R.plurals.my_tickets_requested_lost_on_sale_header, size, size)

            val lostAdapter = LostOffersAdapter()
            requested_lost_on_sale_list.adapter = lostAdapter
            if (requested_lost_on_sale_list.itemDecorationCount == 0) {
                requested_lost_on_sale_list.addItemDecoration(TransactionOffersItemDecoration(itemView.resources.getDimensionPixelSize(R.dimen.margin_medium)))
            }
            (requested_lost_on_sale_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            lostAdapter.submitList(lostOffers)
        } else {
            container_lost_on_sale.isVisible = false
        }

        val lostListOffers = lostOffers.filter { it is TransactionOfferItem.LostListOfferItem }
        if (lostListOffers.isNotEmpty()) {
            container_lost_list.isVisible = true

            val size = lostListOffers.size
            requested_lost_list_header.text = itemView.resources.getQuantityString(R.plurals.my_tickets_requested_lost_list_header, size, size)

            val lostAdapter = LostOffersAdapter()
            requested_lost_list_list.adapter = lostAdapter
            if (requested_lost_list_list.itemDecorationCount == 0) {
                requested_lost_list_list.addItemDecoration(TransactionOffersItemDecoration(itemView.resources.getDimensionPixelSize(R.dimen.margin_medium)))
            }
            (requested_lost_list_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            lostAdapter.submitList(lostOffers)
        } else {
            container_lost_list.isVisible = false
        }
    }

    protected class LostOffersAdapter : BaseAdapter<TransactionOfferItem, BaseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return when (viewType) {
                TYPE_LOST_LIST -> LostListOffersViewHolder(parent)
                TYPE_LOST_ON_SALE -> LostOnSaleOffersViewHolder(parent)
                else -> throw IllegalStateException("No view type provided.")
            }
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            when (holder) {
                is LostListOffersViewHolder -> holder.bind(getItem(position) as LostListOfferItem)
                is LostOnSaleOffersViewHolder -> holder.bind(getItem(position) as LostOnSaleOfferItem)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val item = getItem(position)
            return when (item) {
                is LostListOfferItem -> TYPE_LOST_LIST
                is LostOnSaleOfferItem -> TYPE_LOST_ON_SALE
                else -> super.getItemViewType(position)
            }
        }

        override fun createDiffCallback() = object : DiffUtil.ItemCallback<TransactionOfferItem>() {
            override fun areItemsTheSame(oldItem: TransactionOfferItem?, newItem: TransactionOfferItem?): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TransactionOfferItem?, newItem: TransactionOfferItem?): Boolean {
                return oldItem == newItem
            }
        }

        companion object {
            private const val TYPE_LOST_LIST = 31
            private const val TYPE_LOST_ON_SALE = 32
        }
    }

    protected class LostListOffersViewHolder(parent: ViewGroup)
        : BaseViewHolder(inflateView(R.layout.item_requested_transaction_lost, parent)) {

        fun bind(item: TransactionOfferItem.LostListOfferItem) {
            requested_lost_description.text = itemView.resources.getString(R.string.event_landing_transaction_description,
                item.ticketCount, item.description)
        }
    }

    protected class LostOnSaleOffersViewHolder(parent: ViewGroup)
        : BaseViewHolder(inflateView(R.layout.item_requested_transaction_lost, parent)) {

        fun bind(item: TransactionOfferItem.LostOnSaleOfferItem) {
            requested_lost_description.text = itemView.resources.getString(R.string.my_tickets_requested_on_sale_pending,
                item.ticketCount, item.currency, item.amount, item.description)
        }
    }
}