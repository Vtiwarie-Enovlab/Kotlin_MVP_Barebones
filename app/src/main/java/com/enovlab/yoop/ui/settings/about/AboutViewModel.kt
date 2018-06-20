package com.enovlab.yoop.ui.settings.about

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

class AboutViewModel
@Inject constructor() : StateViewModel<AboutView>() {

    override fun start() {
        view?.showVersion(BuildConfig.VERSION_NAME)
    }

    internal fun termsClicked() {
        view?.showTermsAndConditionsWebPage(BuildConfig.LINK_TERMS_AND_CONDITIONS)
    }

    internal fun privacyPolicyClicked() {
        view?.showTermsAndConditionsWebPage(BuildConfig.LINK_PRIVACY_POLICY)
    }
}