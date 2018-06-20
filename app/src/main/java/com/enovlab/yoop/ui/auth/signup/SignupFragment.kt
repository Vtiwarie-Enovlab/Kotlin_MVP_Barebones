package com.enovlab.yoop.ui.auth.signup

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.auth.signup.step.Step
import com.enovlab.yoop.ui.auth.signup.step.email.EmailFragment
import com.enovlab.yoop.ui.auth.signup.step.email.EmailViewModel
import com.enovlab.yoop.ui.auth.signup.step.name.NameFragment
import com.enovlab.yoop.ui.auth.signup.step.name.NameViewModel
import com.enovlab.yoop.ui.auth.signup.step.password.PasswordFragment
import com.enovlab.yoop.ui.auth.signup.step.password.PasswordViewModel
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.layout_appbar_auth.*

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class SignupFragment : AuthFragment<SignupView, SignupViewModel>(), SignupView {
    override val viewModelOwner = ViewModelOwner.ACTIVITY
    override val vmClass = SignupViewModel::class.java

    private val nameViewModel by lazy { obtainViewModel(NameViewModel::class.java) }
    private val emailViewModel by lazy { obtainViewModel(EmailViewModel::class.java) }
    private val passwordViewModel by lazy { obtainViewModel(PasswordViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        back.setOnClickListener { onBackPressed() }

        childFragmentManager.addOnBackStackChangedListener {
            val fragmentManager = childFragmentManager
            viewModel.step = Step.valueOf(fragmentManager.findFragmentById(CONTAINER).tag!!)
            viewModel.backstackChanged(fragmentManager.backStackEntryCount)
        }

        next_step.setOnClickListener {
            viewModel.nextStepClicked(next_step.state!!)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.initialStep()
    }

    override fun showBackButton(show: Boolean) {
        back.isVisible = show
    }

    override fun showCurrentStep(step: Int, size: Int) {
        current_step.text = "${step}/$size"
    }

    override fun showNextStepEnabled(enabled: Boolean) {
        next_step.state = if (enabled) State.ENABLED else State.DISABLED
    }

    override fun showNextStepLoading(active: Boolean) {
        next_step.state = if (active) State.LOADING else State.ENABLED
    }

    override fun showNextStepLoadingSuccess() {
        next_step.state = State.SUCCESS
    }

    override fun showNameValidateInput() {
        nameViewModel.validateInput()
    }

    override fun showEmailValidateInput() {
        emailViewModel.validateInput()
    }

    override fun showEmailAddressCheck() {
        emailViewModel.checkEmailAddress()
    }

    override fun showCreateAccount() {
        passwordViewModel.createAccount(nameViewModel.firstName!!,
            nameViewModel.lastName!!, emailViewModel.email!!)
    }

    override fun createSnackbar(text: String, actionText: String?, action: (() -> Unit)?) {
        showSnackbarAction(container_step, text, actionText, action)
    }

    override fun removeSnackbar() {
        hideSnackbar()
    }

    override fun showAccountCreated() {
        navigator.navigateToVerifyEmailSignup.go(emailViewModel.email!! to 1500L)
    }

    override fun showNotes(show: Boolean) {
        notes.isInvisible = show.not()
    }

    override fun showEmailInputFieldsClearedFocus() {
        emailViewModel.clearInputFieldsFocus()
    }

    override fun showPasswordInputFieldsClearedFocus() {
        passwordViewModel.clearInputFieldsFocus()
    }

    override fun showNameScreen() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, NameFragment.newInstance(), Step.NAME.name)
            .addToBackStack(null)
            .commit()
    }

    override fun showEmailScreen() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, EmailFragment.newInstance(), Step.EMAIL.name)
            .addToBackStack(null)
            .commit()
    }

    override fun showPasswordScreen() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, PasswordFragment.newInstance(), Step.PASSWORD.name)
            .addToBackStack(null)
            .commit()
    }

    fun onBackPressed() {
        childFragmentManager.popBackStack()
    }

    companion object {
        fun newInstance() = SignupFragment()

        private const val CONTAINER = R.id.container_signup
    }
}