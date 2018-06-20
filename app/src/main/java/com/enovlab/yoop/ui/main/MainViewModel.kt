package com.enovlab.yoop.ui.main

import androidx.core.net.toUri
import com.enovlab.yoop.data.entity.enums.OfferStatus
import com.enovlab.yoop.data.entity.enums.OfferSubStatus
import com.enovlab.yoop.data.ext.emailVerified
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.NotificationsRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.main.mytickets.MyTicketsNavigation
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by mtosk on 3/7/2018.
 */
class MainViewModel
@Inject constructor(private val userRepository: UserRepository,
                    private val authRepository: AuthRepository,
                    private val eventsRepository: EventsRepository,
                    private val notificationsRepository: NotificationsRepository) : StateViewModel<MainView>() {

    override fun start() {
        preferences.navigation?.let { navigation ->
            when (navigation) {
                MyTicketsNavigation.SECURED.name,
                MyTicketsNavigation.REQUESTED.name,
                MyTicketsNavigation.INBOX.name -> view?.showMyTickets(MyTicketsNavigation.valueOf(navigation))
            }
            preferences.navigation = null
        }

        disposables += notificationsRepository.observeUnreadNotifications().subscribe({
            view?.showNotificationsBadge(it > 0)
        }, { error ->
            Timber.e(error)
        })
    }

    internal fun verifySignupEmail(token: String) {
        disposables += userRepository.user().subscribe({ user ->
            if (!user.emailVerified()) {
                action {
                    authRepository.verifySignupEmail(token).toCompletable()
                        .doOnError {
                            view?.showAuthSignupVerificationExpired(user.email)
                        }
                        .doOnComplete {
                            continueAssignment()
                        }
                }
            } else {
                continueAssignment()
            }
        }, { error ->
            view?.showAuthLogin()
            Timber.e(error, "Error loading user from local db.")
        })
    }

    internal fun verifyResetPasswordEmail(token: String) {
        action {
            authRepository.verifyResetPasswordEmail(token).toCompletable()
                .doOnComplete {
                    view?.showAuthResetPassword(token)
                }
                .doOnError {
                    val email = preferences.resetEmail
                    if (email != null) view?.showAuthResetPasswordVerificationExpired(email)
                }
        }
    }

    internal fun saveTokenAssignmentDeepLink(deepLink: String) {
        preferences.assignmentDeepLink = deepLink
    }

    internal fun checkTransactionDetails(eventId: String,
                                         marketplaceId: String) {
        action {
            eventsRepository.loadUserEvent(eventId)
                .observeOn(schedulers.main)
                .doAfterSuccess {
                    val marketplace = it.marketplaceInfo?.find { it.id == marketplaceId }
                    if (marketplace != null) {
                        val currentDate = Date()
                        if (currentDate >= marketplace.startDate && currentDate <= marketplace.endDate) {
                            view?.showTransactionDetails(eventId, marketplace.type!!.name)
                        } else {
                            view?.showEventLanding(eventId)
                        }
                    } else {
                        view?.showEventLanding(eventId)
                    }
                }
                .toCompletable()
        }
    }

    internal fun checkTransactionEdit(eventId: String,
                                      marketplaceId: String,
                                      offerGroupId: String) {
        action {
            eventsRepository.loadUserEvent(eventId)
                .observeOn(schedulers.main)
                .doAfterSuccess {
                    val marketplace = it.marketplaceInfo?.find { it.id == marketplaceId }
                    if (marketplace != null) {
                        val currentDate = Date()
                        if (currentDate >= marketplace.startDate && currentDate <= marketplace.endDate) {
                            view?.showTransactionEdit(eventId, marketplace.type!!.name, offerGroupId)
                        } else {
                            view?.showEventLanding(eventId)
                        }
                    } else {
                        view?.showEventLanding(eventId)
                    }
                }
                .toCompletable()
        }
    }

    internal fun checkTransactionReview(eventId: String,
                                        marketplaceId: String,
                                        offerGroupId: String, offerId: String) {
        action {
            eventsRepository.loadUserEvent(eventId)
                .observeOn(schedulers.main)
                .doAfterSuccess {
                    val marketplace = it.marketplaceInfo?.find { it.id == marketplaceId }
                    if (marketplace != null) {
                        val offerGroup = marketplace.offerGroups?.find { it.offer?.id == offerId }
                        if (offerGroup?.offer != null) {
                            val offer = offerGroup.offer!!

                            // claim
                            if (offer.displayArchive == false
                                && offer.offerStatus == OfferStatus.WON_MANUAL_PAYMENT_REQUIRED
                                && offer.offerSubStatus == null) {

                                view?.showTransactionReviewClaim(eventId, marketplace.type!!.name, offerGroup.id)
                            }
                            // payment failed
                            else if (offer.displayArchive == false
                                && offer.offerStatus == OfferStatus.WON_MANUAL_PAYMENT_REQUIRED
                                && offer.offerSubStatus == OfferSubStatus.AUTO_PAYMENT_FAILED) {

                                view?.showTransactionReviewFix(eventId, marketplace.type!!.name, offerGroup.id)
                            }
                            // fallback
                            else {
                                view?.showEventLanding(eventId)
                            }
                        }
                    } else {
                        view?.showEventLanding(eventId)
                    }
                }
                .toCompletable()
        }
    }

    private fun continueAssignment() {
        if (preferences.assignmentDeepLink != null) {
            preferences.signedUp = true
            view?.showTokenAssignment(preferences.assignmentDeepLink!!.toUri())
        }
    }
}