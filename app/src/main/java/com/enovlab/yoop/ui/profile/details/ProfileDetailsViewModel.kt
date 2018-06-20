package com.enovlab.yoop.ui.profile.details

import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.entity.user.User
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject

class ProfileDetailsViewModel
@Inject constructor(private val repository: UserRepository,
                    private val authRepository: AuthRepository) : StateViewModel<ProfileDetailsView>() {

    private lateinit var user: User

    private var firstNameChanged = false
    private var lastNameChanged = false

    override fun start() {
        observeLocalUser()
        refresh { repository.refreshUser().toCompletable() }
    }

    private fun observeLocalUser() {
        singleSubscription = repository.user().subscribe({ user ->
            this.user = user

            view?.showFirstName(user.firstName ?: "")
            view?.showLastName(user.lastName ?: "")
            view?.showEmailAddress(user.email)
            view?.showPhoto(user.photo)

            val verified = user.photoVerified == true
            view?.showVerified(verified)
            view?.showVerifiedCaption(verified)
            view?.showVerifiedTitle(user.eventReady == true)
            view?.showVerifiedPhoto(verified)

        }, {
            Timber.e(it)
        })
    }

    internal fun firstNameChanged(firstName: String) {
        firstNameChanged = user.firstName != firstName && Validator.FIRST_NAME.validate(firstName)
        view?.showSaveChanges(firstNameChanged || lastNameChanged)
    }

    internal fun lastNameChanged(lastName: String) {
        lastNameChanged = user.lastName != lastName && Validator.LAST_NAME.validate(lastName)
        view?.showSaveChanges(firstNameChanged || lastNameChanged)
    }

    internal fun saveChanges(firstName: String, lastName: String) {
        action {
            repository.updateUser(firstName, lastName, user.locale!!).toCompletable().doOnComplete {
                view?.showEdittingFinished()
            }
        }
    }

    internal fun changePassword() {
        view?.showPasswordChange(false)
        action {
            authRepository.forgotPassword(user.email)
                .observeOn(schedulers.main).doOnComplete {
                    view?.showPasswordChanged(true)
                }
        }
    }

    internal fun logOut() {
        action {
            authRepository.logout().doOnComplete {
                disposables += dataManager.clear().subscribeOn(schedulers.disk).subscribe({
                    view?.showEdittingFinished()
                }, { error ->
                    Timber.e(error)
                })
            }
        }
    }

    internal fun capturePhotoClicked() {
        when {
            preferences.introSeen -> view?.showCapture()
            else -> view?.showIntro()
        }
    }
}