package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.OfferStatus
import com.enovlab.yoop.data.entity.enums.OfferSubStatus
import java.util.*

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

data class Offer (

    @ColumnInfo(name = "offer_id")
    var id: String,

    var retryEndTime: Date?,

    var retryAttemptsLeft: Int?,

    @ColumnInfo(name = "offer_tokens")
    var numberOfTokens: Int?,

    var offerStatus: OfferStatus?,

    var offerSubStatus: OfferSubStatus?,

    var chance: Chance?,

    var cardType: String?,

    var lastDigits: String?,

    var amount: Double?,

    var displayArchive: Boolean?,

    @Embedded
    var tokenPrice: TokenPrice?
)
