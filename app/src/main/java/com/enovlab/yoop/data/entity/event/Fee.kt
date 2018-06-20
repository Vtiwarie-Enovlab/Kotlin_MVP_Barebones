package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.ColumnInfo

data class Fee(
    @ColumnInfo(name = "fee_amount") val amount: Double?,
    @ColumnInfo(name = "fee_value") val value: Double?,
    @ColumnInfo(name = "fee_name") val name: String?
)