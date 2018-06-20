package com.enovlab.yoop.api.response

import com.enovlab.yoop.data.entity.enums.OfferStatus

data class OnSaleOfferResponse(
        val offerGroupId: String,
        val offerStatus: OfferStatus,
        val numberOfTokenResponse: TokenResponse
)