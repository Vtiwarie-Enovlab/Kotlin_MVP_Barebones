package com.enovlab.yoop.api.response

import java.util.*

/**
 * Created by mtosk on 3/19/2018.
 */
data class EventSearch(val eventId: String,
                       val eventName: String,
                       val locationName: String,
                       val eventDate: Date)