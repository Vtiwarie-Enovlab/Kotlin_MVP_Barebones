package com.enovlab.yoop.api.response.payment

data class SingleUseToken(var id: String? = null,
                          var card: Card? = null,
                          var profileId: String? = null,
                          var paymentToken: String? = null,
                          var timeToLiveSeconds: Int? = null)
