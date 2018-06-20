package com.enovlab.yoop.ui.profile.intro

import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

class IntroViewModel
@Inject constructor() : StateViewModel<IntroView>() {

    override fun start() {
        when {
            preferences.introSeen -> view?.showCloseIntro()
            else -> preferences.introSeen = true
        }
    }
}