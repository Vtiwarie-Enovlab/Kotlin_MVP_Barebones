package com.enovlab.yoop.ui.auth.verify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.base.ViewModelOwner
import kotlinx.android.synthetic.main.fragment_verify.*
import kotlinx.android.synthetic.main.layout_appbar_auth.*

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
class VerificationFragment : AuthFragment<VerificationView, VerificationViewModel>(), VerificationView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = VerificationViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.type = arguments?.getString(ARG_TYPE)?.let { VerificationType.valueOf(it) }
        viewModel.email = arguments?.getString(ARG_EMAIL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }

        open_inbox.setOnClickListener { navigator.navigateToInbox.go() }
        resend_link.setOnClickListener { viewModel.resendLink() }
    }

    override fun showTitleSignUp() {
        headline.setText(R.string.auth_verify_signup_headline)
    }

    override fun showTitleForgotPassword() {
        headline.setText(R.string.auth_verify_forgot_password_headline)
    }

    override fun showEmail(email: String) {
        email_title.text = resources.getString(R.string.auth_verify_email, email)
    }

    companion object {
        fun newInstance(email: String, type: VerificationType) = VerificationFragment().apply {
            arguments = Bundle(2).apply {
                putString(ARG_EMAIL, email)
                putString(ARG_TYPE, type.name)
            }
        }

        private const val ARG_TYPE = "ARG_TYPE"
        private const val ARG_EMAIL = "ARG_EMAIL"
    }
}