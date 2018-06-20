package com.enovlab.yoop.ui.transaction.review.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.ui.base.list.BaseAdapter

/**
 * Created by mtosk on 3/8/2018.
 */
class PaymentsAdapter : BaseAdapter<PaymentMethod, PaymentsViewHolder>() {

    var listener: ((PaymentMethod) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentsViewHolder {
        return PaymentsViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: PaymentsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<PaymentMethod>() {
        override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem == newItem
        }
    }
}