package com.enovlab.yoop.api.request

data class OnSalePricingRequest(val offerGroupId: String,
                                val numTokens: Int,
                                val amount: Int,
                                val token: String?,
                                val autoPay: Boolean = false)
