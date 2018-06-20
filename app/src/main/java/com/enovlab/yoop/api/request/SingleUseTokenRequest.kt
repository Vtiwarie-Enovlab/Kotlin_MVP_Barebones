package com.enovlab.yoop.api.request

import com.enovlab.yoop.api.response.payment.Card

/**
 * Created by Max Toskhoparan on 1/12/2018.
 */
data class SingleUseTokenRequest(val card: Card)