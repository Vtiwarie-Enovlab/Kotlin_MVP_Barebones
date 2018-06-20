package com.enovlab.yoop.ui.main.mytickets.secured.adapter

import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import java.util.*

sealed class SecuredTokens(open val eventId: String) {

    object UnVerifiedSecuredItem : SecuredTokens("")

    data class SecuredTokenItem(override val eventId: String,
                                val mediaUrl: String,
                                val eventName: String,
                                val eventDate: Date,
                                val locationName: String,
                                val items: List<TokenItem>,
                                val firstItem: TokenItem,
                                var expanded: Boolean = false,
                                val pendingCount: Int = 0) : SecuredTokens(eventId) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SecuredTokenItem

            if (eventId != other.eventId) return false
            if (mediaUrl != other.mediaUrl) return false
            if (eventName != other.eventName) return false
            if (eventDate != other.eventDate) return false
            if (locationName != other.locationName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = eventId.hashCode()
            result = 31 * result + mediaUrl.hashCode()
            result = 31 * result + eventName.hashCode()
            result = 31 * result + eventDate.hashCode()
            result = 31 * result + locationName.hashCode()
            return result
        }
    }

    data class PendingTokenItem(override val eventId: String,
                                val mediaUrl: String,
                                val eventName: String,
                                val eventDate: Date,
                                val locationName: String,
                                val pendingCount: Int,
                                var expanded: Boolean = false) : SecuredTokens(eventId) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PendingTokenItem

            if (eventId != other.eventId) return false
            if (mediaUrl != other.mediaUrl) return false
            if (eventName != other.eventName) return false
            if (eventDate != other.eventDate) return false
            if (locationName != other.locationName) return false
            if (pendingCount != other.pendingCount) return false

            return true
        }

        override fun hashCode(): Int {
            var result = eventId.hashCode()
            result = 31 * result + mediaUrl.hashCode()
            result = 31 * result + eventName.hashCode()
            result = 31 * result + eventDate.hashCode()
            result = 31 * result + locationName.hashCode()
            result = 31 * result + pendingCount.hashCode()
            return result
        }
    }
}