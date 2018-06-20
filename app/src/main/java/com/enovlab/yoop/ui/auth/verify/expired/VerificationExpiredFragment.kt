package com.enovlab.yoop.ui.auth.verify.expired

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.auth.verify.VerificationType
import com.enovlab.yoop.ui.base.ViewModelOwner
import kotlinx.android.synthetic.main.fragment_verify_expired.*
import kotlinx.android.synthetic.main.layout_appbar_auth.*

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class VerificationExpiredFragment : AuthFragment<VerificationExpiredView, VerificationExpiredViewModel>(), VerificationExpiredView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = VerificationExpiredViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.type = arguments?.getString(ARG_TYPE)?.let { VerificationType.valueOf(it) }
        viewModel.email = arguments?.getString(ARG_EMAIL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_expired, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        send_link.setOnClickListener { viewModel.resendLink() }
    }

    companion object {
        fun newInstance(email: String?, type: VerificationType) = VerificationExpiredFragment().apply {
            arguments = Bundle(2).apply {
                putString(ARG_EMAIL, email)
                putString(ARG_TYPE, type.name)
            }
        }

        private const val ARG_TYPE = "ARG_TYPE"
        private const val ARG_EMAIL = "ARG_EMAIL"
    }
}