package com.enovlab.yoop.data.entity.event

import com.enovlab.yoop.data.entity.enums.EventMediaType
import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

data class DefaultMedia (

    var url: String?,

    @SerializedName("mediaType")
    var mediaType: EventMediaType?
)
