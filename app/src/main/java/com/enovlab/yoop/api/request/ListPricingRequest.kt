package com.enovlab.yoop.api.request

data class ListPricingRequest(val offerGroupId: String,
                              val numTokens: Int,
                              val autoPay: Boolean = false)

//{
//    "autoPay": true,
//    "offerGroupId": 0,
//    "amount": 0,
//    "numTokens": 0,
//    "token": "string"
//}