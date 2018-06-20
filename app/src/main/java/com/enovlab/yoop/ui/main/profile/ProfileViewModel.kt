package com.enovlab.yoop.ui.main.profile

import com.enovlab.yoop.data.ext.emailVerified
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProfileViewModel
@Inject constructor(private val repository: UserRepository,
                    private val authRepository: AuthRepository) : StateViewModel<ProfileView>() {

    override fun start() {
        observeLocalUser()
        if (repository.isAuthorized()) {
            load { repository.refreshUser().toCompletable() }
        }
    }

    private fun observeLocalUser() {
        if (repository.isAuthorized()) { // state Authorized
            disposables += repository.user().subscribe({ user ->
                view?.showUnauthorized(false)
                view?.showEmailAddress(user.email)
                view?.showUsername("${user.firstName} ${user.lastName}")

                if (user.emailVerified()) { // state Email verified
                    view?.showEmailNotVerified(false)
                    view?.showPaymentMethods(true)
                    view?.showPreferences(true)

                    if (user.eventReady == true) { // state Email verified, has photo
                        view?.showEmailVerifiedNoPhoto(false)
                        view?.showUserPhoto(user.photo)

                        if (user.photoVerified == true) {
                            view?.showPendingVerification(false)
                            view?.showVerified(true)
                            view?.showVerificationDate(DATE_FORMAT.format(user.verificationDate))

                            val verificationSeen = user.verificationSeen == true

                            view?.showSteps(!verificationSeen)
                            view?.showStepSignup(true)
                            view?.showStepReady(true)
                            view?.showStepVerified(true)
                            view?.showVerifiedBackgroundEnabled(verificationSeen)

                            if (!verificationSeen) {
                                action { repository.seenProfileVerification() }
                            }
                        } else { // state pending verification
                            view?.showPendingVerification(true)

                            view?.showSteps(true)
                            view?.showStepSignup(true)
                            view?.showStepReady(true)
                            view?.showStepVerified(false)
                            view?.showVerifiedBackgroundEnabled(false)
                        }
                    } else { // state Email verified, no photo
                        view?.showEmailVerifiedNoPhoto(true)

                        view?.showSteps(true)
                        view?.showStepSignup(true)
                        view?.showStepReady(false)
                        view?.showStepVerified(false)
                        view?.showVerifiedBackgroundEnabled(false)
                    }
                } else { // state Email not verified
                    view?.showEmailNotVerified(true)

                    view?.showPaymentMethods(false)
                    view?.showPreferences(false)
                    view?.showSteps(true)
                    view?.showStepSignup(false)
                    view?.showStepReady(false)
                    view?.showStepVerified(false)
                    view?.showVerifiedBackgroundEnabled(false)
                }
            }, { error ->
                Timber.e(error)
            })
        } else { // state Unauthorized
            view?.showUnauthorized(true)
            view?.showEmailNotVerified(false)
            view?.showEmailVerifiedNoPhoto(false)
            view?.showPendingVerification(false)
            view?.showVerified(false)

            view?.showPaymentMethods(false)
            view?.showPreferences(false)
            view?.showSteps(true)
            view?.showStepSignup(false)
            view?.showStepReady(false)
            view?.showStepVerified(false)
            view?.showVerifiedBackgroundEnabled(false)
        }
    }

    internal fun createIdClicked() {
        when {
            preferences.introSeen -> view?.showProfileCapture()
            else -> view?.showProfileIntro()
        }
    }

    internal fun resendVerificationLink() {
        action {
            authRepository.resendVerificationLink()
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMM d", Locale.getDefault())
    }

//    enum class State {
//        UNAUTHORIZED,
//        EMAIL_UNVERIFIED,
//        EMAIL_VERIFIED_NO_PHOTO,
//        PENDING_VERIFICATION,
//        VERIFIED_FIRST_TIME,
//        VERIFIED
//    }
}