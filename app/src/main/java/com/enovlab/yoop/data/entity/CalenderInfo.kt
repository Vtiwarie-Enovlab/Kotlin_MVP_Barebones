package com.enovlab.yoop.data.entity

import java.util.*

/**
 * Created by vishaantiwarie on 3/16/18.
 */
data class CalenderInfo(
        var eventId: String,
        var deepLinkUrl: String,
        var eventName: String,
        var eventStartDate: Date? = null,
        var eventEndDate: Date? = null,
        var eventDescription: String? = null,
        var eventLocation: String? = null
)