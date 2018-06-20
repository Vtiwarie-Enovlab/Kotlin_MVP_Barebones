package com.enovlab.yoop.ui.auth

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.auth.AuthNavigator.Navigation
import com.enovlab.yoop.ui.auth.forgotpass.ForgotPasswordFragment
import com.enovlab.yoop.ui.auth.login.LoginFragment
import com.enovlab.yoop.ui.auth.resetpass.ResetPasswordFragment
import com.enovlab.yoop.ui.auth.signup.SignupFragment
import com.enovlab.yoop.ui.auth.verify.VerificationFragment
import com.enovlab.yoop.ui.auth.verify.VerificationType
import com.enovlab.yoop.ui.auth.verify.expired.VerificationExpiredFragment
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.widget.YoopSnackbar
import com.enovlab.yoop.utils.WeakHandler

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class AuthActivity : BaseActivity<AuthNavigator>() {
    override val navigatorClass = AuthNavigator::class.java

    private lateinit var handler: WeakHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        handler = WeakHandler()

        onNewIntent(intent)
    }

    override fun setupNavigation(navigator: AuthNavigator) {
        navigator.navigateToSignup.observeNavigation { navigateToSignup() }
        navigator.navigateToLogin.observeNavigation { navigateToLogin() }
        navigator.navigateToVerifyEmailSignup.observeNavigation {
            handler.postDelayed({ navigateToVerification(it.first, VerificationType.SIGN_UP) }, it.second)
        }
        navigator.navigateToVerifyEmailForgotPassword.observeNavigation {
            handler.postDelayed({ navigateToVerification(it.first, VerificationType.RESET_PASSWORD) }, it.second)
        }
        navigator.navigateBack.observeNavigation {
            handler.postDelayed({ navigateBack(it.first) }, it.second)
        }

        navigator.navigateToInbox.observeNavigation { navigateToInbox() }
        navigator.navigateToForgotPassword.observeNavigation { navigateToForgotPassword() }
        navigator.navigateToWebUrl.observeNavigation { navigateToWebUrl(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val email = intent?.getStringExtra(EXTRA_EMAIL)
        val token = intent?.getStringExtra(EXTRA_TOKEN)
        val navigation = intent?.getStringExtra(EXTRA_NAVIGATION)?.let { Navigation.valueOf(it) }
        when (navigation) {
            Navigation.LOGIN -> navigateToLogin()
            Navigation.SIGNUP -> navigateToSignup()
            Navigation.SIGNUP_VERIFICATION_EXPIRED -> navigateToVerificationExpired(email, VerificationType.SIGN_UP)
            Navigation.RESET_PASSWORD_VERIFICATION_EXPIRED -> navigateToVerificationExpired(email, VerificationType.RESET_PASSWORD)
            Navigation.RESET_PASSWORD -> navigateToResetPassword(token)
        }
    }

    private fun navigateToSignup() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, SignupFragment.newInstance())
            .commit()
    }

    private fun navigateToLogin() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, LoginFragment.newInstance())
            .commit()
    }

    private fun navigateToVerification(email: String, type: VerificationType) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, VerificationFragment.newInstance(email, type))
            .commit()
    }

    private fun navigateToForgotPassword() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, ForgotPasswordFragment.newInstance())
            .commit()
    }

    private fun navigateToVerificationExpired(email: String?, type: VerificationType) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, VerificationExpiredFragment.newInstance(email, type))
            .commit()
    }

    private fun navigateToResetPassword(token: String?) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, ResetPasswordFragment.newInstance(token))
            .commit()
    }

    private fun navigateToWebUrl(url: String?) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setToolbarColor(ContextCompat.getColor(this, R.color.color_white))
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun navigateToInbox() {
        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val errorMessage = getString(R.string.auth_verify_no_default_email)

        try {
            if(intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                YoopSnackbar.make(window.decorView).text(errorMessage).show()
            }
        } catch (e: ActivityNotFoundException) {
            YoopSnackbar.make(window.decorView).text(errorMessage).show()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(CONTAINER)
        when (fragment) {
            is SignupFragment -> when {
                fragment.childFragmentManager.backStackEntryCount <= 1 -> super.onBackPressed()
                else -> fragment.onBackPressed()
            }
            else -> super.onBackPressed()
        }
    }

    companion object {
        private val CONTAINER = R.id.container_auth
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
        const val EXTRA_TOKEN = "EXTRA_TOKEN"
        const val EXTRA_NAVIGATION = "EXTRA_NAVIGATION"
    }
}