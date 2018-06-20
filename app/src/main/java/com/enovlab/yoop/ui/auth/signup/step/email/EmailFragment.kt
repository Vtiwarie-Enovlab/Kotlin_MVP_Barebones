package com.enovlab.yoop.ui.auth.signup.step.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.signup.step.StepFragment
import kotlinx.android.synthetic.main.fragment_signup_email.*

/**
 * Created by mtosk on 3/5/2018.
 */
class EmailFragment : StepFragment<EmailView, EmailViewModel>(), EmailView {
    override val vmClass = EmailViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        input_email.textChangeListener(viewModel::onEmailInputChanged)
        input_email.focus()
        input_email.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hostViewModel.removeSnackbar()
        }
    }

    override fun showEmailAddress(emailAddress: String) {
        input_email.setText(emailAddress)
    }

    override fun showEmailValid(valid: Boolean) {
        input_email.isValid(valid)
        if (valid) hostViewModel.removeSnackbar()
    }

    override fun showEmailError() {
        input_email.errorEnabled(true)
        hostViewModel.createSnackbar(getString(R.string.auth_signup_email_valid_error))
    }

    override fun showInputValid(valid: Boolean) {
        hostViewModel.inputValidation(valid)
    }

    override fun showInputFieldsClearedFocus() {
        input_email.clearFocus()
    }

    override fun showActionIndicator(active: Boolean) {
        hostViewModel.loadingStarted(active)
    }

    override fun showError(message: String?) {
        hostViewModel.createSnackbar(getString(R.string.auth_signup_email_error),
            getString(R.string.auth_login_label), { navigator.navigateToLogin.go() })
    }

    override fun showErrorNoConnection() {
        hostViewModel.createSnackbar(getString(R.string.connection_error))
    }

    override fun showSuccessAction() {
        hostViewModel.emailCheckedSuccess()
    }

    override fun showInputFieldsEnabled(enabled: Boolean) {
        input_email.isEnabled = enabled
    }

    companion object {
        fun newInstance() = EmailFragment()
    }
}