package com.enovlab.yoop.ui.auth.signup.step.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.signup.step.StepFragment
import kotlinx.android.synthetic.main.fragment_signup_password.*

/**
 * Created by mtosk on 3/5/2018.
 */
class PasswordFragment : StepFragment<PasswordView, PasswordViewModel>(), PasswordView {
    override val vmClass = PasswordViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        input_password.textChangeListener(viewModel::onPasswordInputChanged)
        input_password.focus()
        input_password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hostViewModel.removeSnackbar()
        }
    }

    override fun showInputValid(valid: Boolean) {
        hostViewModel.inputValidation(valid)
    }

    override fun showPasswordValid(valid: Boolean) {
        input_password.isValid(valid)
    }

    override fun showHas8CharactersValid(valid: Boolean) {
        input_password.minCharactersValid(valid)
    }

    override fun showHasNumbersValid(valid: Boolean) {
        input_password.digitsValid(valid)
    }

    override fun showInputFieldsClearedFocus() {
        input_password.clearFocus()
    }

    override fun showActionIndicator(active: Boolean) {
        hostViewModel.loadingStarted(active)
    }

    override fun showError(message: String?) {
        if (message != null) hostViewModel.createSnackbar(message)
    }

    override fun showErrorNoConnection() {
        hostViewModel.createSnackbar(getString(R.string.connection_error))
    }

    override fun showSuccessAction() {
        hostViewModel.accountCreated()
    }

    override fun showInputFieldsEnabled(enabled: Boolean) {
        input_password.isEnabled = enabled
    }

    companion object {
        fun newInstance() = PasswordFragment()
    }
}