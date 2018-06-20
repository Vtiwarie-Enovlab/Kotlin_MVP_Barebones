package com.enovlab.yoop.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import com.enovlab.yoop.ui.widget.YoopSnackbar
import com.enovlab.yoop.utils.ext.hideKeyboard
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.layout_appbar_auth.*

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class LoginFragment : AuthFragment<LoginView, LoginViewModel>(), LoginView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = LoginViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        forgot_password.setOnClickListener { navigator.navigateToForgotPassword.go() }

        input_email.textChangeListener(viewModel::emailInputChanged)
        input_email.focus()

        input_password.textChangeListener(viewModel::passwordInputChanged)

        input_email.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideSnackbar()
        }
        input_password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideSnackbar()
        }

        next_step.setOnClickListener { viewModel.nextStepClicked(next_step.state) }
    }

    override fun showEmailValid(valid: Boolean) {
        if (valid) hideSnackbar()
    }

    override fun showEmailError() {
        input_email.errorEnabled(true)
        showSnackbar(container_step, getString(R.string.auth_signup_email_valid_error), true)
    }

    override fun showPasswordError() {
        input_password.errorEnabled(true)
    }

    override fun showInputValid(valid: Boolean) {
        next_step.state = if (valid) State.ENABLED else State.DISABLED
    }

    override fun showInputFieldsClearedFocus() {
        input_email.clearFocus()
        input_password.clearFocus()
    }

    override fun showSuccessAction() {
        next_step.state = State.SUCCESS
        navigator.navigateBack.go(true to 1500L)
    }

    override fun showActionIndicator(active: Boolean) {
        next_step.state = if (active) State.LOADING else State.ENABLED
    }

    override fun showInputFieldsEnabled(enabled: Boolean) {
        input_email.isEnabled = enabled
        input_password.isEnabled = enabled
    }

    override fun showError(message: String?) {
        if (message != null) showSnackbar(container_step, message, true)
    }

    override fun showErrorNoConnection() {
        showSnackbar(container_step, getString(R.string.connection_error), true)
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}