package com.enovlab.yoop.api.response

import com.enovlab.yoop.data.entity.event.TokenPrice

data class PricingResponse(val tokenPrice: TokenPrice,
                           val numberOfTokens: Int,
                           val token: String)