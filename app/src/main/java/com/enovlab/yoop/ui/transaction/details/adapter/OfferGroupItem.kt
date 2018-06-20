package com.enovlab.yoop.ui.transaction.details.adapter

import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.Demand

sealed class OfferGroupItem(open val id: String, open val hasUserActivity: Boolean) {

    data class ListOfferGroup(override val id: String,
                              val description: String?,
                              val currency: String?,
                              val userPhoto: String?,
                              override val hasUserActivity: Boolean,
                              val userTickets: Int,
                              val ticketCount: Int,
                              val listPrice: Int) : OfferGroupItem(id, hasUserActivity)

    data class OnSaleOfferGroup(override val id: String,
                                val description: String?,
                                val currency: String?,
                                val userPhoto: String?,
                                override val hasUserActivity: Boolean,
                                val userTickets: Int,
                                val averageOfferPrice: Int,
                                val minAskPrice: Int,
                                val demand: Demand?,
                                val chance: Chance? = null,
                                val minOfferPrice: Int? = null) : OfferGroupItem(id, hasUserActivity)
}