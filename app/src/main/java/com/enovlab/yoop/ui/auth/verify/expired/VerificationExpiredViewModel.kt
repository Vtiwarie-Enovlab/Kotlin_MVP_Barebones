package com.enovlab.yoop.ui.auth.verify.expired

import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.auth.verify.VerificationType
import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class VerificationExpiredViewModel
@Inject constructor(private val repository: AuthRepository) : StateViewModel<VerificationExpiredView>() {

    internal var type: VerificationType? = null
    internal var email: String? = null

    internal fun resendLink() {
        when (type) {
            VerificationType.SIGN_UP -> {
                action {
                    repository.resendVerificationLink()
                }
            }
            VerificationType.RESET_PASSWORD -> {
                action {
                    repository.forgotPassword(email!!)
                }
            }
        }
    }
}