package com.enovlab.yoop.ui.main.mytickets.secured

import com.enovlab.yoop.data.entity.enums.AssignmentStatus
import com.enovlab.yoop.data.entity.enums.OfferStatus
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.TokenInfo
import com.enovlab.yoop.data.entity.user.UserInfo
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens.SecuredTokenItem
import com.enovlab.yoop.utils.ext.toCompletable
import timber.log.Timber
import javax.inject.Inject

class SecuredViewModel
@Inject constructor(private val repository: EventsRepository) : StateViewModel<SecuredView>() {

    override fun start() {
        observeLocalUserEvents()
        loadUserEvents()
    }

    private fun loadUserEvents(refresh: Boolean = false) {
        when {
            refresh -> refresh { repository.loadUserEvents().toCompletable() }
            else -> action { repository.loadUserEvents().toCompletable() }
        }
    }

    private fun observeLocalUserEvents() {
        singleSubscription?.dispose()
        singleSubscription = repository.observeUserEvents()
            .observeOn(schedulers.disk)
            .map { mapToAdapterItems(it) }
            .observeOn(schedulers.main)
            .subscribe({ data ->
                val tokens = data.first
                val userPhotoVerified = data.second
                var photo = data.third

                when {
                    tokens.isNotEmpty() -> {
                        view?.showUnverifiedBanner(userPhotoVerified == false)
                        view?.showEmptyEvents(false)
                    }
                    else -> {
                        view?.showEmptyEvents(true)
                        view?.showProfilePic(photo)
                    }
                }
                view?.showSecuredItems(tokens)
            }, { error ->
                Timber.e(error)
            })
    }

    internal fun refresh() {
        loadUserEvents(true)
    }

    private fun mapToAdapterItems(events: List<Event>): Triple<List<SecuredTokens>, Boolean, String?> {

        val cardItems = mutableListOf<SecuredTokens>()

        for (event in events) {
            val tokens = getTokens(event.tokenInfo, event.assigneeTokenInfo, event.userInfo!!)
            val pending = pendingTokens(event)

            if (tokens.isNotEmpty()) {
                cardItems.add(SecuredTokenItem(event.id, event.defaultMedia?.url ?: "", event.name!!, event.date!!, event.locationName!!, tokens.drop(1), tokens.first(), false, pendingCount = pending))
            } else if (pending > 0) {
                cardItems.add(SecuredTokens.PendingTokenItem(event.id, event.defaultMedia?.url ?: "", event.name!!, event.date!!, event.locationName!!, pending))
            }
        }

        var userInfo: UserInfo? = null
        if (events.isNotEmpty()) {
            userInfo = events.first().userInfo
        }

        if (cardItems.isNotEmpty() && userInfo?.eventReady == false) {
            cardItems.add(0, SecuredTokens.UnVerifiedSecuredItem)
        }

        return Triple(cardItems, userInfo?.photoVerified == true, userInfo?.photo)
    }

    private fun pendingTokens(event: Event): Int {
        var count = 0
        val marketplaces = event.marketplaceInfo?.sortedBy { it.startDate }
        if (marketplaces != null) {
            for (marketplace in marketplaces) {
                val offerGroups = marketplace.offerGroups
                offerGroups?.forEach {
                    if (it.offer != null && it.offer?.displayArchive == false && it.offer?.offerStatus == OfferStatus.WON_PAYMENT_SUCCESSFUL) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun getTokens(tokenInfo: List<TokenInfo>?,
                          assigneeTokenInfo: List<TokenInfo>?,
                          user: UserInfo): List<TokenItem> {

        val tokens = mutableListOf<TokenInfo>().apply {
            if (tokenInfo != null && tokenInfo.isNotEmpty())
                addAll(tokenInfo)
            if (assigneeTokenInfo != null && assigneeTokenInfo.isNotEmpty())
                addAll(assigneeTokenInfo)
        }

        val items = mutableListOf<TokenItem>()

        if (tokens.isEmpty())
            return items

        // setup user's own token
        // get first token from the assignment
        val userToken = tokens.find { it.selfAssigned == true }
        if (userToken != null) {
            items.add(createUserTokenItem(userToken, user))
            tokens.remove(userToken)
        }

        // setup rest tokens
        tokens.forEach { token ->
            items.add(createAssigneeTokenItem(token))
        }
        return items
    }

    private fun createUserTokenItem(token: TokenInfo, user: UserInfo): TokenItem {
        return when {
            user.photoVerified == true -> TokenItem.UserVerifiedTokenItem(token.id, token.section!!, user.photo!!)
            user.eventReady == true -> TokenItem.UserEventReadyTokenItem(token.id, token.section!!, user.photo!!)
            else -> TokenItem.UserNoPhotoTokenItem(token.id, token.section!!)
        }
    }

    private fun createAssigneeTokenItem(token: TokenInfo): TokenItem {
        val assignment = token.tokenAssignment

        // count as unassigned token
        if (assignment == null || assignment.assignmentStatus?.unassigned() == true) {
            return TokenItem.UnassignedTokenItem(token.id, token.section!!)
        }

        // pending accepting
        if (assignment.assignmentStatus == AssignmentStatus.PENDING) {
            return TokenItem.AssigneePendingTokenItem(token.id, token.section!!, assignment.email!!)
        }

        // token accepted
        return when {
            assignment.photoVerified == true -> TokenItem.AssigneeVerifiedTokenItem(token.id, token.section!!, assignment.firstName!!, assignment.photo!!)
            assignment.eventReady == true -> TokenItem.AssigneeEventReadyTokenItem(token.id, token.section!!, assignment.firstName!!, assignment.photo!!)
            else -> TokenItem.AssigneeNoPhotoTokenItem(token.id, token.section!!, assignment.firstName!!)
        }
    }

    internal fun assignmentClicked(eventId: String, ticketId: String) {
        view?.showTicketDetails(eventId, ticketId)
    }

    internal fun createIdClicked() {
        when {
            preferences.introSeen -> view?.showProfileCapture()
            else -> view?.showProfileIntro()
        }
    }
}