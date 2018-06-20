package com.enovlab.yoop.ui.auth.verify

import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
class VerificationViewModel
@Inject constructor(private val repository: AuthRepository) : StateViewModel<VerificationView>() {

    internal var type: VerificationType? = null
    internal var email: String? = null

    override fun start() {
        when (type) {
            VerificationType.SIGN_UP -> view?.showTitleSignUp()
            VerificationType.RESET_PASSWORD -> view?.showTitleForgotPassword()
        }
        if (email != null) view?.showEmail(email!!)
    }

    internal fun resendLink() {
        when (type) {
            VerificationType.SIGN_UP -> action { repository.resendVerificationLink() }
            VerificationType.RESET_PASSWORD -> action { repository.forgotPassword(email!!) }
        }
    }
}