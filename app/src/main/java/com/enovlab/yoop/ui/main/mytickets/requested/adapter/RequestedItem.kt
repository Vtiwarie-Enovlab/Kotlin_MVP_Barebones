package com.enovlab.yoop.ui.main.mytickets.requested.adapter

import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem

sealed class RequestedItem(open val id: String,
                           open val name: String?,
                           open val mediaUrl: String?,
                           open val date: String?,
                           open val location: String?) {

    var offers: List<TransactionOfferItem> = emptyList()
    var lostOffers: List<TransactionOfferItem> = emptyList()

    data class ActiveListItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?,
        val marketplaceEndDate: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class ActiveOnSaleItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?,
        val marketplaceEndDate: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class OpensListItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?,
        val marketplaceEndDate: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class OpensOnSaleItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?,
        val marketplaceEndDate: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class TryListItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?,
        val marketplaceEndDate: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class TryOnSaleItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?,
        val marketplaceEndDate: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class ClosedItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class PendingListItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class PendingOnSaleItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class ActionRequiredListItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?) : RequestedItem(id, name, mediaUrl, date, location)

    data class ActionRequiredOnSaleItem(
        override val id: String,
        override val name: String?,
        override val mediaUrl: String?,
        override val date: String?,
        override val location: String?) : RequestedItem(id, name, mediaUrl, date, location)
}