package com.enovlab.yoop.ui.auth.signup

import android.arch.lifecycle.Lifecycle
import com.enovlab.yoop.ui.auth.signup.step.Step
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import com.enovlab.yoop.utils.TestUtils
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

/**
 * Created by mtosk on 3/8/2018.
 */

class SignupViewModelTest {

    val schedulers = TestUtils.createTestSchedulers()
    val view = mock(SignupView::class.java)
    val lifecycle = mock(Lifecycle::class.java)

    val viewModel = SignupViewModel()

    @Before
    fun before() {
        viewModel.attachView(view, lifecycle)
    }

    @Test
    fun verifyInitialStepIsShown() {
        viewModel.initialStep()
        verify(view).showNameScreen()
    }

    @Test
    fun verifyCurrentStepIsShown() {
        val step = Step.NAME
        viewModel.step = step
        verify(view).showCurrentStep(step.step, Step.values().size)
    }

    @Test
    fun verify_Step_Name_nextStepClicked_State_Enabled() {
        val step = Step.NAME
        val state = State.ENABLED

        viewModel.step = step

        viewModel.nextStepClicked(state)

        verify(view).hideKeyboard()
        verify(view).showEmailScreen()
    }

    @Test
    fun verify_Step_Name_nextStepClicked_State_Disabled() {
        val step = Step.NAME
        val state = State.DISABLED

        viewModel.step = step

        viewModel.nextStepClicked(state)

        verify(view).hideKeyboard()
        verify(view).showNameValidateInput()
    }

    @Test
    fun verify_Step_Email_nextStepClicked_State_Enabled() {
        val step = Step.EMAIL
        val state = State.ENABLED

        viewModel.step = step

        viewModel.nextStepClicked(state)

        verify(view).hideKeyboard()
        verify(view).showEmailInputFieldsClearedFocus()
        verify(view).showEmailAddressCheck()

        viewModel.loadingStarted(true)
        verify(view).showNextStepLoading(true)

        viewModel.loadingStarted(false)
        verify(view).showNextStepLoading(false)

        viewModel.emailCheckedSuccess()

        verify(view).showPasswordScreen()
    }

    @Test
    fun verify_Step_Email_nextStepClicked_State_Disabled() {
        val step = Step.EMAIL
        val state = State.DISABLED

        viewModel.step = step

        viewModel.nextStepClicked(state)

        verify(view).hideKeyboard()
        verify(view).showEmailInputFieldsClearedFocus()
        verify(view).showEmailValidateInput()

        viewModel.inputValidation(true)
        verify(view).showNextStepEnabled(true)
    }

    @Test
    fun verify_Step_Password_nextStepClicked_State_Enabled() {
        val step = Step.PASSWORD
        val state = State.ENABLED

        viewModel.step = step

        viewModel.nextStepClicked(state)

        verify(view).hideKeyboard()
        verify(view).showPasswordInputFieldsClearedFocus()
        verify(view).showCreateAccount()

        viewModel.loadingStarted(true)
        verify(view).showNextStepLoading(true)

        viewModel.loadingStarted(false)
        verify(view).showNextStepLoading(false)

        viewModel.accountCreated()

        verify(view).showNextStepLoadingSuccess()
        verify(view).showAccountCreated()
    }

    @Test
    fun verifyBackstackChanged_OneEntry() {
        viewModel.step = Step.NAME
        viewModel.backstackChanged(1)

        verify(view).showBackButton(false)
        verify(view).removeSnackbar()
        verify(view).showNotes(true)
    }

    @Test
    fun verifyBackstackChanged_TwoEntry() {
        viewModel.step = Step.EMAIL
        viewModel.backstackChanged(2)

        verify(view).showBackButton(true)
        verify(view).removeSnackbar()
        verify(view).showNotes(false)
    }
}