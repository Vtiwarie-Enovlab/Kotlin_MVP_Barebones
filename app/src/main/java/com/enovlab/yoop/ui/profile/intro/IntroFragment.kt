package com.enovlab.yoop.ui.profile.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.profile.ProfileEditFragment
import kotlinx.android.synthetic.main.fragment_profile_edit_intro.*

class IntroFragment : ProfileEditFragment<IntroView, IntroViewModel>(), IntroView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = IntroViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_edit_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intro_back.setOnClickListener { navigator.navigateBack.go(false to 0L) }
        intro_capture.setOnClickListener { navigator.navigateToCapture.go() }
    }

    override fun showCloseIntro() {
        navigator.navigateBack.go(false to 0L)
    }

    companion object {
        fun newInstance() = IntroFragment()
    }
}