package com.enovlab.yoop.ui.transaction.ticket.transfer.contacts

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.data.entity.Contact
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder

class ContactsAdapter : LoadingAdapter<Contact>() {

    var listener: ((Contact) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            ListItem.Type.LOADING.ordinal -> LoadingViewHolder(parent)
            else -> ContactsViewHolder(parent, listener)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is ContactsViewHolder -> holder.bind(getItem(position))
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            else -> ListItem.Type.CONTACTS.ordinal
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact?, newItem: Contact?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Contact?, newItem: Contact?): Boolean {
            return oldItem == newItem
        }
    }
}