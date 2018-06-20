package com.enovlab.yoop.ui.payments.manage.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder


/**
 * @author vishaan
 */
class ManagePaymentsAdapter : LoadingAdapter<PaymentItem>() {

    var newCardListener: (() -> Unit)? = null
    var editCardListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE.LOADING.ordinal -> LoadingViewHolder(parent)
            TYPE.EDIT_CARD.ordinal -> EditCardViewHolder(parent, editCardListener)
            else -> return AddNewCardViewHolder(parent, newCardListener)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is AddNewCardViewHolder -> holder.bind()
            is EditCardViewHolder -> holder.bind(getItem(position))
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val paymentItem = getItem(position)

        return when {
            isLoadingType(position) -> TYPE.LOADING.ordinal
            paymentItem.methodItem is PaymentItem.PaymentMethodItem.PaymentMethod -> TYPE.EDIT_CARD.ordinal
            else -> TYPE.ADD_NEW_CARD.ordinal
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<PaymentItem>() {
        override fun areItemsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
            return oldItem.methodItem == newItem.methodItem
        }
    }

    override fun onInserted(position: Int, count: Int) {
        when {
            isLoading -> notifyDataSetChanged()
            else -> super.onInserted(position, count)
        }
    }

    private enum class TYPE {
        LOADING,
        ADD_NEW_CARD,
        EDIT_CARD
    }
}