package com.enovlab.yoop

import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.event.*
import com.enovlab.yoop.data.entity.user.MobileCountry
import com.enovlab.yoop.data.entity.user.User
import java.util.*

/**
 * Created by Max Toskhoparan on 2/16/2018.
 */
object TestUtils {

    fun createUser(email: String, firstName: String, lastName: String,
                   mobileCountry: MobileCountry? = null,
                   paymentMethods: List<PaymentMethod>? = null): User {

        return User(email, firstName, lastName, null, null, null, null, null,null, null, null, null, mobileCountry).apply {
            this.paymentMethods = paymentMethods
        }
    }

    fun createMobileCountry(countryName: String, countryCode: String, phoneCode: String): MobileCountry {
        return MobileCountry(countryName, countryCode, phoneCode)
    }

    fun createPaymentMethods(id: String, userId: String, count: Int): List<PaymentMethod> {
        val list = mutableListOf<PaymentMethod>()
        for (i in 0 until count)
            list.add(PaymentMethod(id + i, userId, null, null, null, null, null, null, null, isDefault = i == 0))
        return list
    }

    fun createMarketplaceInfo(count: Int, id: Int): List<MarketplaceInfo> {
        val list = mutableListOf<MarketplaceInfo>()
        for (i in 0 until count)
            list.add(
                createMarketplaceInfo("Marketplace" + i + id, listOf(
                    createOfferGroup("OfferGroup" + i + id, createOffer("Offer" + i + id)),
                    createOfferGroup("OfferGroup" + i + i + id, createOffer("Offer" + i + i + id)))
                )
            )
        return list
    }

    fun createTokenInfo(count: Int, id: Int): List<TokenInfo> {
        val list = mutableListOf<TokenInfo>()
        for (i in 0 until count)  {
            list.add(createTokenInfo("TokenInfo" + i + id,
                createOffer("Offer" + i + id), createTokenAssignment("TokenAssignment" + i + id)))
        }
        return list
    }

    fun createEvents(count: Int, discoverable: Boolean? = null, userActivity: Boolean? = null): List<Event> {
        val list = mutableListOf<Event>()
        for (i in 0 until count) {
            val event = Event("Event" + i, "Name" + i, null, null,
                null, null, null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, null, null,
                null, null, discoverable, userActivity, null, null, null, Date(),
                null, null, null, null)
            list.add(event)
        }
        return list
    }

    private fun createMarketplaceInfo(id: String, offerGroups: List<OfferGroup>): MarketplaceInfo {
        return MarketplaceInfo(id, null, null, null, null, null, null, null, null, null, null, null, null).apply {
            this.offerGroups = offerGroups
        }
    }

    private fun createOfferGroup(id: String, offer: Offer): OfferGroup {
        return OfferGroup(id, null, null, null, null, null, null, null, null, null, null, null, offer)
    }

    private fun createOffer(id: String): Offer {
        return Offer(id, null, null, null, null, null, null, null, null, null, null, null)
    }

    private fun createTokenAssignment(id: String): TokenAssignment {
        return TokenAssignment(id, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    }

    private fun createTokenInfo(id: String, offer: Offer, tokenAssignment: TokenAssignment): TokenInfo {
        return TokenInfo(id, null, null, null, null, offer, tokenAssignment, null, null)
    }
}