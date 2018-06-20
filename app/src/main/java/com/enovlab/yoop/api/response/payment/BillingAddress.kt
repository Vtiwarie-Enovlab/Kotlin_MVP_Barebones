package com.enovlab.yoop.api.response.payment

data class BillingAddress(var street: String? = null,
                          var street2: String? = null,
                          var city: String? = null,
                          var state: String? = null,
                          var country: String? = null,
                          var zip: String? = null,
                          var phone: String? = null)
