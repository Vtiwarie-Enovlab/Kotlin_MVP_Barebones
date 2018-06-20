package com.enovlab.yoop.ui.transaction.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseAdapter
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.CustomTimer
import java.util.*

class TransactionOffersAdapter : BaseAdapter<TransactionOfferItem, BaseViewHolder>() {

    var listenerEdit: ((TransactionOfferItem) -> Unit)? = null
    var listenerFix: ((TransactionOfferItem) -> Unit)? = null
    var listenerClaim: ((TransactionOfferItem) -> Unit)? = null
    var listenerAddNew: (() -> Unit)? = null

    private val timers = mutableMapOf<String, CustomTimer?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_ACTIVE_LIST -> OffersActiveListViewHolder(parent, listenerEdit)
            TYPE_ACTIVE_ON_SALE -> OffersActiveOnSaleViewHolder(parent, listenerEdit)
            TYPE_PENDING_LIST -> OffersPendingListViewHolder(parent)
            TYPE_PENDING_ON_SALE -> OffersPendingOnSaleViewHolder(parent)
            TYPE_PAYMENT_FAILED_LIST -> OffersPaymentFailedListViewHolder(parent, listenerFix)
            TYPE_PAYMENT_FAILED_ON_SALE -> OffersPaymentFailedOnSaleViewHolder(parent, listenerFix)
            TYPE_SELECTED_LIST -> OffersSelectedListViewHolder(parent, listenerClaim)
            TYPE_SELECTED_ON_SALE -> OffersSelectedOnSaleViewHolder(parent, listenerClaim)
            TYPE_PAID_PENDING_LIST -> OffersPaidPendingListViewHolder(parent)
            TYPE_PAID_PENDING_ON_SALE -> OffersPaidPendingOnSaleViewHolder(parent)
            TYPE_ACTIVE_NEW -> OffersActiveNewViewHolder(parent, listenerAddNew)
            else -> throw IllegalStateException("No view type provided.")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is OffersActiveListViewHolder -> holder.bind(getItem(position) as TransactionOfferItem.ActiveListOfferItem)
            is OffersActiveOnSaleViewHolder -> holder.bind(getItem(position) as TransactionOfferItem.ActiveOnSaleOfferItem)
            is OffersPendingListViewHolder -> holder.bind(getItem(position) as TransactionOfferItem.PendingListOfferItem)
            is OffersPendingOnSaleViewHolder -> holder.bind(getItem(position) as TransactionOfferItem.PendingOnSaleOfferItem)
            is OffersPaymentFailedListViewHolder -> {
                val item = getItem(position) as TransactionOfferItem.PaymentFailedListOfferItem
                updateTimer(item.id, item.fixEndTime)
                holder.bind(item, timers[item.id])
            }
            is OffersPaymentFailedOnSaleViewHolder -> {
                val item = getItem(position) as TransactionOfferItem.PaymentFailedOnSaleOfferItem
                updateTimer(item.id, item.fixEndTime)
                holder.bind(item, timers[item.id])
            }
            is OffersSelectedListViewHolder -> {
                val item = getItem(position) as TransactionOfferItem.SelectedListOfferItem
                updateTimer(item.id, item.claimEndTime)
                holder.bind(item, timers[item.id])
            }
            is OffersSelectedOnSaleViewHolder -> {
                val item = getItem(position) as TransactionOfferItem.SelectedOnSaleOfferItem
                updateTimer(item.id, item.claimEndTime)
                holder.bind(item, timers[item.id])
            }
            is OffersPaidPendingListViewHolder -> holder.bind(getItem(position) as TransactionOfferItem.PaidPendingListOfferItem)
            is OffersPaidPendingOnSaleViewHolder -> holder.bind(getItem(position) as TransactionOfferItem.PaidPendingOnSaleOfferItem)
            is OffersActiveNewViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is TransactionOfferItem.ActiveListOfferItem -> TYPE_ACTIVE_LIST
            is TransactionOfferItem.ActiveOnSaleOfferItem -> TYPE_ACTIVE_ON_SALE
            is TransactionOfferItem.PendingListOfferItem -> TYPE_PENDING_LIST
            is TransactionOfferItem.PendingOnSaleOfferItem -> TYPE_PENDING_ON_SALE
            is TransactionOfferItem.PaymentFailedListOfferItem -> TYPE_PAYMENT_FAILED_LIST
            is TransactionOfferItem.PaymentFailedOnSaleOfferItem -> TYPE_PAYMENT_FAILED_ON_SALE
            is TransactionOfferItem.SelectedListOfferItem -> TYPE_SELECTED_LIST
            is TransactionOfferItem.SelectedOnSaleOfferItem -> TYPE_SELECTED_ON_SALE
            is TransactionOfferItem.PaidPendingListOfferItem -> TYPE_PAID_PENDING_LIST
            is TransactionOfferItem.PaidPendingOnSaleOfferItem -> TYPE_PAID_PENDING_ON_SALE
            is TransactionOfferItem.ActiveListNewOfferItem,
            is TransactionOfferItem.ActiveOnSaleNewOfferItem -> TYPE_ACTIVE_NEW
            else -> super.getItemViewType(position)
        }
    }

    private fun updateTimer(id: String, date: Date?) {
        timers[id]?.cancel()
        when {
            date != null && date > Date() -> timers[id] = CustomTimer(date)
            else -> timers[id] = null
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<TransactionOfferItem>() {
        override fun areItemsTheSame(oldItem: TransactionOfferItem, newItem: TransactionOfferItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TransactionOfferItem, newItem: TransactionOfferItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        timers.forEach { _, timer -> timer?.cancel() }
    }

    companion object {
        private const val TYPE_ACTIVE_LIST = 21
        private const val TYPE_ACTIVE_ON_SALE = 22
        private const val TYPE_PENDING_LIST = 23
        private const val TYPE_PENDING_ON_SALE = 24
        private const val TYPE_PAYMENT_FAILED_LIST = 25
        private const val TYPE_PAYMENT_FAILED_ON_SALE = 26
        private const val TYPE_SELECTED_LIST = 27
        private const val TYPE_SELECTED_ON_SALE = 28
        private const val TYPE_PAID_PENDING_LIST = 29
        private const val TYPE_PAID_PENDING_ON_SALE = 30
        private const val TYPE_ACTIVE_NEW = 31
    }
}