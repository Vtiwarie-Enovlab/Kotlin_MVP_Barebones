package com.enovlab.yoop.ui.settings.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.fragment_settings_about.*
import kotlinx.android.synthetic.main.layout_settings_app_bar.*

class AboutFragment : SettingsFragment<AboutView, AboutViewModel>(), AboutView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = AboutViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings_back.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        settings_title.setText(R.string.settings_title_about)

        about_terms.setOnClickListener { viewModel.termsClicked() }
        about_privacy.setOnClickListener { viewModel.privacyPolicyClicked() }
    }

    override fun showVersion(version: String) {
        about_version.text = getString(R.string.settings_about_version, version)
    }

    override fun showPrivacyPolicyWebPage(url: String?) {
        navigator.navigateToWebUrl.go(url)
    }

    override fun showTermsAndConditionsWebPage(url: String?) {
        navigator.navigateToWebUrl.go(url)
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}