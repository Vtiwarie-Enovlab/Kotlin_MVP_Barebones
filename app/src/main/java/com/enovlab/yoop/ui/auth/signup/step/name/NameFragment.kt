package com.enovlab.yoop.ui.auth.signup.step.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.signup.step.StepFragment
import kotlinx.android.synthetic.main.fragment_signup_name.*

/**
 * Created by mtosk on 3/5/2018.
 */
class NameFragment : StepFragment<NameView, NameViewModel>(), NameView {
    override val vmClass = NameViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        input_first_name.textChangeListener(viewModel::onFirstNameInputChanged)
        input_first_name.focus()
        input_last_name.textChangeListener(viewModel::onLastNameInputChanged)
    }

    override fun showFirstName(firstName: String) {
        input_first_name.setText(firstName)
    }

    override fun showLastName(lastName: String) {
        input_last_name.setText(lastName)
    }

    override fun showFirstNameValid(valid: Boolean) {
        input_first_name.isValid(valid)
    }

    override fun showLastNameValid(valid: Boolean) {
        input_last_name.isValid(valid)
    }

    override fun showFirstNameError() {
        input_first_name.errorEnabled(true)
    }

    override fun showLastNameError() {
        input_last_name.errorEnabled(true)
    }

    override fun showInputValid(valid: Boolean) {
        hostViewModel.inputValidation(valid)
    }

    companion object {
        fun newInstance() = NameFragment()
    }
}