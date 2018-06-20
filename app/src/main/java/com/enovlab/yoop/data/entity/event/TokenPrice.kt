package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.Embedded

data class TokenPrice (

    var totalAmount: Double?,

    var totalFees: Double?,

    var totalTax: Double?,

    var totalTokenAmount: Double?,

    @Embedded
    var fee: Fee?
)
