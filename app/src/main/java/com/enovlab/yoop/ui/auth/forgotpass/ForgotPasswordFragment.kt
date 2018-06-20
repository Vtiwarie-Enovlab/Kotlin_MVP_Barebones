package com.enovlab.yoop.ui.auth.forgotpass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.layout_appbar_auth.*

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class ForgotPasswordFragment : AuthFragment<ForgotPasswordView, ForgotPasswordViewModel>(), ForgotPasswordView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = ForgotPasswordViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }

        next_step.setOnClickListener { viewModel.nextStepClicked(next_step.state!!) }

        input_email.textChangeListener(viewModel::emailInputChanged)
        input_email.focus()
        input_email.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideSnackbar()
        }
    }

    override fun showEmailValid(valid: Boolean) {
        input_email.isValid(valid)
        if (valid) hideSnackbar()
    }

    override fun showEmailError() {
        input_email.errorEnabled(true)
        showSnackbar(next_step, getString(R.string.auth_signup_email_valid_error), true)
    }

    override fun showInputValid(valid: Boolean) {
        next_step.state = if (valid) State.ENABLED else State.DISABLED
    }

    override fun showInputFieldsClearedFocus() {
        input_email.clearFocus()
    }

    override fun showSuccessAction() {
        next_step.state = State.SUCCESS
        navigator.navigateToVerifyEmailForgotPassword.go(input_email.getText() to 1500L)
    }

    override fun showActionIndicator(active: Boolean) {
        next_step.state = if (active) State.LOADING else State.ENABLED
    }

    override fun showInputFieldsEnabled(enabled: Boolean) {
        input_email.isEnabled = enabled
    }

    override fun showError(message: String?) {
        if (message != null)
            showSnackbarAction(next_step, message, getString(R.string.auth_signup_label), { navigator.navigateToSignup.go() })
    }

    override fun showErrorNoConnection() {
        showSnackbar(next_step, getString(R.string.connection_error), true)
    }

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }
}