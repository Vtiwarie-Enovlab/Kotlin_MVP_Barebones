package com.enovlab.yoop.ui.transaction.adapter

import com.enovlab.yoop.data.entity.enums.Chance
import java.util.*

sealed class TransactionOfferItem(open val id: String) {

    data class ActiveListOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val ticketCount: Int,
        val listPrice: Int) : TransactionOfferItem(id)

    data class ActiveOnSaleOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val ticketCount: Int,
        val amount: Int,
        val chance: Chance?) : TransactionOfferItem(id)

    data class LostListOfferItem(
        override val id: String,
        val description: String?,
        val ticketCount: Int) : TransactionOfferItem(id)

    data class LostOnSaleOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val amount: Int,
        val ticketCount: Int) : TransactionOfferItem(id)

    data class SelectedListOfferItem(
        override val id: String,
        val description: String?,
        val ticketCount: Int,
        val claimEndTime: Date?) : TransactionOfferItem(id)

    data class SelectedOnSaleOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val amount: Int,
        val ticketCount: Int,
        val claimEndTime: Date?) : TransactionOfferItem(id)

    data class PendingListOfferItem(
        override val id: String,
        val description: String?,
        val ticketCount: Int) : TransactionOfferItem(id)

    data class PendingOnSaleOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val ticketCount: Int,
        val amount: Int) : TransactionOfferItem(id)

    data class PaymentFailedListOfferItem(
        override val id: String,
        val description: String?,
        val ticketCount: Int,
        val fixEndTime: Date?) : TransactionOfferItem(id)

    data class PaymentFailedOnSaleOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val ticketCount: Int,
        val amount: Int,
        val fixEndTime: Date?) : TransactionOfferItem(id)

    data class PaidPendingListOfferItem(
        override val id: String,
        val description: String?,
        val ticketCount: Int) : TransactionOfferItem(id)

    data class PaidPendingOnSaleOfferItem(
        override val id: String,
        val description: String?,
        val currency: String?,
        val ticketCount: Int,
        val amount: Int) : TransactionOfferItem(id)

    object ActiveListNewOfferItem : TransactionOfferItem("")

    object ActiveOnSaleNewOfferItem : TransactionOfferItem("")
}