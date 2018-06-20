package com.enovlab.yoop.ui.transaction.ticket.accept

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.data.entity.event.Performer
import com.enovlab.yoop.data.ext.emailVerified
import com.enovlab.yoop.data.repository.AssignmentRepository
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import com.enovlab.yoop.utils.ext.toCompletable
import timber.log.Timber
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TicketAcceptViewModel
@Inject constructor(private val eventsRepository: EventsRepository,
                    private val userRepository: UserRepository,
                    private val authRepository: AuthRepository,
                    private val assignmentRepository: AssignmentRepository) : StateViewModel<TicketAcceptView>() {

    internal lateinit var id: String
    internal lateinit var assignmentToken: String
    internal var email: String? = null

    private var assignmentState = AssignmentState.VERIFY_TOKEN
    private var userEmail: String? = null

    override fun start() {
        startAssignment()

        observeEvent()
        load { eventsRepository.observeEvent(id).toCompletable() }
    }

    internal fun startAssignment() {
        resetStates()
        when (assignmentState) {
            AssignmentState.VERIFY_TOKEN -> verifyTokenValid()
            AssignmentState.VERIFY_AUTHORIZATION -> verifyAuthorization()
            AssignmentState.VERIFY_TOKENS_ASSIGNED -> verifyTokensAssigned()
            AssignmentState.CONFIRMATION -> confirmAssignment()
        }
    }

    internal fun closeAssignment() {
        when (assignmentState) {
            AssignmentState.VERIFY_AUTHORIZATION,
            AssignmentState.CONFIRMATION -> view?.showCloseAssignmentDialog()
            else -> view?.showClosedAssignment()
        }
    }

    internal fun closeAssignmentConfirmed() {
        view?.showClosedAssignment()
    }

    internal fun goToMyTicket() {
        view?.showTicketClaimed(id)
    }

    internal fun resendLink() {
        load { authRepository.resendVerificationLink() }
    }

    private fun verifyTokenValid() {
        assignmentState = AssignmentState.VERIFY_TOKEN

        action {
            assignmentRepository.verifyAssignmentToken(assignmentToken)
                .doOnSubscribe { view?.showClaimTicketHeadline() }
                .doOnError { error ->
                    if (error !is UnknownHostException) { //TODO bad
                        resetStates()
                        view?.showStateTokenInvalid(true)
                    }
                }
                .doOnComplete { verifyAuthorization() }
        }
    }

    private fun verifyAuthorization() {
        assignmentState = AssignmentState.VERIFY_AUTHORIZATION

        if (userRepository.isAuthorized()) {
            disposables += userRepository.user().subscribe({ user ->
                if (user.emailVerified()) {
                    when {
                        preferences.signedUp -> confirmAssignment()
                        else -> verifyTokensAssigned()
                    }
                } else {
                    userEmail = user.email
                    resetStates()
                    view?.showEmailVerification(userEmail!!)
                    view?.showStateEmailVerification(true)
                }
            }, { error ->
                Timber.e(error)
            })
        } else {
            resetStates()
            view?.showLegalLinks(BuildConfig.LINK_TERMS_AND_CONDITIONS, BuildConfig.LINK_PRIVACY_POLICY)
            view?.showStateNotAuthorized(true)
        }
    }

    private fun verifyTokensAssigned() {
        assignmentState = AssignmentState.VERIFY_TOKENS_ASSIGNED

        action {
            eventsRepository.loadEvent(id)
                .map {
                    (it.tokenInfo != null && it.tokenInfo!!.isNotEmpty())
                        || (it.assigneeTokenInfo != null && it.assigneeTokenInfo!!.isNotEmpty())
                }
                .observeOn(schedulers.main)
                .doOnSubscribe { view?.showClaimTicketHeadline() }
                .doOnSuccess { hasTokens ->
                    when {
                        hasTokens -> {
                            resetStates()
                            view?.showStateTokensAssigned(true)
                        }
                        else -> confirmAssignment()
                    }
                }.toCompletable()
        }
    }

    private fun confirmAssignment() {
        assignmentState = AssignmentState.CONFIRMATION

        action {
            assignmentRepository.confirmAssignment(assignmentToken)
                .doOnSubscribe { view?.showClaimTicketHeadline() }
                .doOnSuccess {
                    view?.showTicketClaimedHeadline()
                    view?.showTicketClaimed(id, 1500L)
                }.toCompletable()
        }

        preferences.assignmentDeepLink = null
        preferences.signedUp = false
    }

    private fun resetStates() {
        view?.showStateTokenInvalid(false)
        view?.showStateNotAuthorized(false)
        view?.showStateEmailVerification(false)
        view?.showStateTokensAssigned(false)
    }

    private fun observeEvent() {
        disposables += eventsRepository.observeEvent(id).subscribe({ event ->
            view?.showEventName(event.shortName)
            view?.showEventDateLocation(DATE_FORMAT_HEADER.format(event.date), event.locationName)

            performerPictureUrl(event.performers)?.let { url ->
                view?.showPerformerPicture(url)
            }
        }, { error ->
            Timber.e(error)
        })
    }

    private fun performerPictureUrl(performers: List<Performer>?): String? {
        if (performers == null || performers.isEmpty()) return null
        return performers.first().defaultMedia?.url
    }

    private enum class AssignmentState {
        VERIFY_TOKEN, VERIFY_AUTHORIZATION, VERIFY_TOKENS_ASSIGNED, CONFIRMATION
    }

    companion object {
        private val DATE_FORMAT_HEADER = SimpleDateFormat("M/d, ha", Locale.getDefault())
    }
}