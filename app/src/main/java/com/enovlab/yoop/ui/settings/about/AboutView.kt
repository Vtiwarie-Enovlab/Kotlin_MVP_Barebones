package com.enovlab.yoop.ui.settings.about

import com.enovlab.yoop.ui.base.state.StateView

interface AboutView : StateView {
    fun showVersion(version: String)
    fun showPrivacyPolicyWebPage(url: String?)
    fun showTermsAndConditionsWebPage(url: String?)
}