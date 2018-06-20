package com.enovlab.yoop.api.response.payment

data class Card(var holderName: String? = null,
                var cardNum: String? = null,
                var cardBin: String? = null,
                var cvv: String? = null,
                var track1: String? = null,
                var track2: String? = null,
                var lastDigits: String? = null,
                var cardExpiry: CardExpiry? = null,
                var cardType: String? = null,
                var billingAddress: BillingAddress? = null)
