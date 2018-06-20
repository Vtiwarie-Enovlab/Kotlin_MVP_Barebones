package com.enovlab.yoop.ui.auth.resetpass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.*
import com.enovlab.yoop.ui.widget.YoopSnackbar
import com.enovlab.yoop.utils.ext.hideKeyboard
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.layout_appbar_auth.*

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
class ResetPasswordFragment : AuthFragment<ResetPasswordView, ResetPasswordViewModel>(), ResetPasswordView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = ResetPasswordViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.token = arguments?.getString(ARG_TOKEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }

        next_step.setOnClickListener { viewModel.nextStepClicked(next_step.state!!) }

        input_password.textChangeListener(viewModel::onPasswordInputChanged)
        input_password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideSnackbar()
        }
    }

    override fun showInputValid(valid: Boolean) {
        next_step.state = if (valid) State.ENABLED else State.DISABLED
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
        next_step.state = if (active) State.LOADING else State.ENABLED
    }

    override fun showSuccessAction() {
        next_step.state = State.SUCCESS
        navigator.navigateBack.go(true to 1500L)
    }

    override fun showInputFieldsEnabled(enabled: Boolean) {
        input_password.isEnabled = enabled
    }

    override fun showError(message: String?) {
        if (message != null) showSnackbar(next_step, message, true)
    }

    override fun showErrorNoConnection() {
        showSnackbar(next_step, getString(R.string.connection_error), true)
    }

    companion object {
        fun newInstance(token: String?) = ResetPasswordFragment().apply {
            arguments = Bundle(1).apply {
                putString(ARG_TOKEN, token)
            }
        }

        private const val ARG_TOKEN = "ARG_TOKEN"
    }
}