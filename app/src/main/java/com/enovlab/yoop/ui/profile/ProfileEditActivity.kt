package com.enovlab.yoop.ui.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.profile.capture.CaptureFragment
import com.enovlab.yoop.ui.profile.details.ProfileDetailsFragment
import com.enovlab.yoop.ui.profile.intro.IntroFragment
import com.enovlab.yoop.utils.WeakHandler

class ProfileEditActivity : BaseActivity<ProfileEditNavigator>() {
    override val navigatorClass = ProfileEditNavigator::class.java

    private lateinit var weakHandler: WeakHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        weakHandler = WeakHandler()

        if (intent.hasExtra(EXTRA_NAVIGATION)) {
            val navigation = ProfileEditNavigator.Navigation.valueOf(intent.getStringExtra(EXTRA_NAVIGATION))
            when (navigation) {
                ProfileEditNavigator.Navigation.DETAILS -> navigateToDetails()
                ProfileEditNavigator.Navigation.CAPTURE -> navigateToCapture()
                ProfileEditNavigator.Navigation.INTRO -> navigateToIntro()
            }
        } else {
            navigateToDetails()
        }
    }

    override fun setupNavigation(navigator: ProfileEditNavigator) {
        navigator.navigateBack.observeNavigation {
            weakHandler.postDelayed({ navigateBack(it.first) }, it.second)
        }
        navigator.navigateToCapture.observeNavigation { navigateToCapture() }
        navigator.navigateToIntro.observeNavigation { navigateToIntro() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(CONTAINER)
        if (fragment is CaptureFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun navigateToDetails() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, ProfileDetailsFragment.newInstance())
            .commit()
    }

    private fun navigateToCapture() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, CaptureFragment.newInstance())
            .commit()
    }

    private fun navigateToIntro() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, IntroFragment.newInstance())
            .commit()
    }

    companion object {
        const val EXTRA_NAVIGATION = "EXTRA_NAVIGATION"
        private val CONTAINER = R.id.container_profile_edit
    }

    enum class NAVIGATION {
        DETAILS, CAPTURE
    }
}