package com.enovlab.yoop.data.manager

import android.net.Uri
import android.os.Build
import android.text.Html
import com.enovlab.yoop.BuildConfig
import timber.log.Timber

object DeepLinkManager {

    // parse deep link uri, returns Discover by default
    fun destination(uri: Uri?): Destination? {
        val encoded = encodeUri(uri) ?: return null

        when (encoded.path) {
            BuildConfig.DEEP_LINK_PATH_EMAIL_VERIFIED -> {
                val token: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_TOKEN)
                if (token != null) return Destination.SignupVerification(token)
            }
            BuildConfig.DEEP_LINK_PATH_CHANGE_PASSWORD -> {
                val token: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_TOKEN)
                if (token != null) return Destination.ResetPasswordVerification(token)
            }
            BuildConfig.DEEP_LINK_PATH_EVENTS -> {
                val eventId: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_EVENT_ID)
                return when (eventId) {
                    null -> Destination.Discover
                    else -> Destination.EventLanding(eventId)
                }
            }
            BuildConfig.DEEP_LINK_PATH_MY_TICKETS -> {
                val eventId: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_EVENT_ID)
                val marketplaceId: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_MARKETPLACE_ID)
                val offerId: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_OFFER_ID)
                val isTicketDetails = encoded.getBooleanQueryParameter(BuildConfig.DEEP_LINK_QUERY_TICKET_DETAILS, false)
                val offerGroupId: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_OFFER_GROUP_ID)

                if (eventId != null) {
                    if (isTicketDetails) {
                        return Destination.TicketDetails(eventId)
                    } else if (marketplaceId != null) {
                        return when {
                            offerGroupId != null && offerId != null -> Destination.TransactionReview(eventId, marketplaceId, offerGroupId, offerId)
                            offerGroupId != null -> Destination.TransactionEdit(eventId, marketplaceId, offerGroupId)
                            else -> Destination.TransactionDetails(eventId, marketplaceId)
                        }
                    }

                    return Destination.EventLanding(eventId)
                }
            }
            BuildConfig.DEEP_LINK_PATH_SECURED -> {
                return Destination.Secured
            }
            BuildConfig.DEEP_LINK_PATH_TOKEN_ASSIGNMENT -> {
                val eventId: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_EVENT_ID)
                val assignmentToken: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_ASSIGNMENT_TOKEN)
                val email: String? = encoded.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_EMAIL)

                if (eventId != null) {
                    if (assignmentToken != null) {
                        return Destination.TokenAssignment(eventId, assignmentToken, email)
                    }

                    return Destination.EventLanding(eventId)
                }
            }
        }

        return null
    }

    fun eventId(url: String): String? {
        try {
            return encodeUri(Uri.parse(url))?.getQueryParameter(BuildConfig.DEEP_LINK_QUERY_EVENT_ID)
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    private fun encodeUri(uri: Uri?): Uri? {
        if (uri?.scheme != BuildConfig.DEEP_LINK_SCHEME || uri.host != BuildConfig.DEEP_LINK_HOST)
            return null

        var encoded = uri
        try {
            val encodedString = fromHtml(uri.toString())
            encoded = Uri.parse(encodedString)
        } catch (e: Exception) {
            Timber.e(e)
        }

        return encoded
    }

    @Suppress("DEPRECATION")
    private fun fromHtml(html: String): String = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        else -> Html.fromHtml(html).toString()
    }

    sealed class Destination {
        // auth
        data class SignupVerification(val token: String) : Destination()
        data class ResetPasswordVerification(val token: String) : Destination()

        // event
        object Discover : Destination()
        data class EventLanding(val eventId: String) : Destination()

        // transactions
        data class TransactionDetails(val eventId: String,
                                      val marketplaceId: String) : Destination()

        data class TransactionEdit(val eventId: String,
                                   val marketplaceId: String,
                                   val offerGroupId: String) : Destination()

        data class TransactionReview(val eventId: String,
                                     val marketplaceId: String,
                                     val offerGroupId: String,
                                     val offerId: String) : Destination()

        // ticket details
        data class TicketDetails(val eventId: String) : Destination()

        // assignment
        data class TokenAssignment(val eventId: String,
                                   val assignmentToken: String, val email: String?) : Destination()

        // secured
        object Secured : Destination()
    }
}