package com.enovlab.yoop.ui.main.discover.adapter

import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.event.Event

/**
 * Created by vishaantiwarie on 3/16/18.
 */
data class DiscoverItem(val event: Event, var pills: List<Pill>) {

    sealed class Pill {
        data class NormalListPill(val price: Double?, val currency: String, val multiple: Boolean = false) : Pill()
        data class NormalOnSalePill(val price: Double?, val currency: String, val multiple: Boolean = true) : Pill()
        data class ActiveListPill(val userPhoto: String?) : Pill()
        data class ActiveOnSalePill(val chance: Chance?, val userPhoto: String?) : Pill()
        data class GoingPill(val userPhoto: String?) : Pill()
        object NewPill : Pill()
    }
}