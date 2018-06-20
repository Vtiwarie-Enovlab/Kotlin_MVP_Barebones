package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams.*
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import com.enovlab.yoop.R

class ProfileStepView : ConstraintLayout {

    private lateinit var stepSignupUnverified: TextView
    private lateinit var stepSignupVerified: ImageView
    private lateinit var stepSignupTitle: TextView

    private lateinit var stepReadyUnverified: TextView
    private lateinit var stepReadyVerified: ImageView
    private lateinit var stepReadyTitle: TextView
    private lateinit var stepReadyProgress: View

    private lateinit var stepVerifiedUnverified: TextView
    private lateinit var stepVerifiedVerified: ImageView
    private lateinit var stepVerifiedTitle: TextView
    private lateinit var stepVerifiedProgress: View

    private var unverifiedTitleColor: Int = 0
    private var verifiedTitleColor: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        unverifiedTitleColor = ContextCompat.getColor(context, R.color.color_white_alpha_50)
        verifiedTitleColor = ContextCompat.getColor(context, R.color.color_white)

        val stepSize = resources.getDimensionPixelSize(R.dimen.profile_step_size)
        val progressSize = resources.getDimensionPixelSize(R.dimen.profile_progress_size)
        val unverifiedBackground = ContextCompat.getDrawable(context, R.drawable.oval_white_alpha_50)
        val verifiedBackground = ContextCompat.getDrawable(context, R.drawable.oval_solid_white)
        val unverifiedTextColor = ContextCompat.getColor(context, R.color.colorAccent)
        val iconPadding = resources.getDimensionPixelSize(R.dimen.padding_extra_small)
        val marginDefault = resources.getDimensionPixelSize(R.dimen.margin_default)
        val marginSmall = resources.getDimensionPixelSize(R.dimen.margin_small)

        // Step Sign Up
        val stepSignupParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val stepSignup = FrameLayout(context)
        stepSignup.id = View.generateViewId()
        stepSignup.layoutParams = stepSignupParams

        val stepSignupUnverifiedParams = FrameLayout.LayoutParams(stepSize, stepSize)
        stepSignupUnverifiedParams.gravity = Gravity.CENTER
        stepSignupUnverified = TextView(context)
        stepSignupUnverified.layoutParams = stepSignupUnverifiedParams
        TextViewCompat.setTextAppearance(stepSignupUnverified, R.style.Text_Bold_Medium)
        stepSignupUnverified.background = unverifiedBackground
        stepSignupUnverified.gravity = Gravity.CENTER
        stepSignupUnverified.setTextColor(unverifiedTextColor)
        stepSignupUnverified.setText(R.string.profile_step_signup_1)

        val stepSignupVerifiedParams = FrameLayout.LayoutParams(stepSize, stepSize)
        stepSignupVerifiedParams.gravity = Gravity.CENTER
        stepSignupVerified = ImageView(context)
        stepSignupVerified.layoutParams = stepSignupVerifiedParams
        stepSignupVerified.background = verifiedBackground
        stepSignupVerified.setImageResource(R.drawable.ic_done_success_24dp)
        stepSignupVerified.updatePaddingRelative(iconPadding, iconPadding, iconPadding, iconPadding)
        stepSignupVerified.isVisible = false

        stepSignup.addView(stepSignupUnverified)
        stepSignup.addView(stepSignupVerified)

        val stepSignupTitleParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        stepSignupTitleParams.marginStart = marginDefault
        stepSignupTitleParams.topMargin = marginSmall
        stepSignupTitle = TextView(context)
        stepSignupTitle.id = View.generateViewId()
        stepSignupTitle.layoutParams = stepSignupTitleParams
        TextViewCompat.setTextAppearance(stepSignupTitle, R.style.Text_Bold_Caption)
        stepSignupTitle.gravity = Gravity.CENTER
        stepSignupTitle.setTextColor(unverifiedTitleColor)
        stepSignupTitle.setAllCaps(true)
        stepSignupTitle.setText(R.string.profile_step_signup)

        // Step Ready
        val stepReadyParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val stepReady = FrameLayout(context)
        stepReady.id = View.generateViewId()
        stepReady.layoutParams = stepReadyParams

        val stepReadyUnverifiedParams = FrameLayout.LayoutParams(stepSize, stepSize)
        stepReadyUnverifiedParams.gravity = Gravity.CENTER
        stepReadyUnverified = TextView(context)
        stepReadyUnverified.layoutParams = stepReadyUnverifiedParams
        TextViewCompat.setTextAppearance(stepReadyUnverified, R.style.Text_Bold_Medium)
        stepReadyUnverified.background = unverifiedBackground
        stepReadyUnverified.gravity = Gravity.CENTER
        stepReadyUnverified.setTextColor(unverifiedTextColor)
        stepReadyUnverified.setText(R.string.profile_step_ready_2)

        val stepReadyVerifiedParams = FrameLayout.LayoutParams(stepSize, stepSize)
        stepReadyVerifiedParams.gravity = Gravity.CENTER
        stepReadyVerified = ImageView(context)
        stepReadyVerified.layoutParams = stepReadyVerifiedParams
        stepReadyVerified.background = verifiedBackground
        stepReadyVerified.setImageResource(R.drawable.ic_done_success_24dp)
        stepReadyVerified.updatePaddingRelative(iconPadding, iconPadding, iconPadding, iconPadding)
        stepReadyVerified.isVisible = false

        stepReady.addView(stepReadyUnverified)
        stepReady.addView(stepReadyVerified)

        val stepReadyTitleParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        stepReadyTitleParams.marginStart = marginSmall
        stepReadyTitleParams.marginEnd = marginSmall
        stepReadyTitleParams.topMargin = marginSmall
        stepReadyTitle = TextView(context)
        stepReadyTitle.id = View.generateViewId()
        stepReadyTitle.layoutParams = stepReadyTitleParams
        TextViewCompat.setTextAppearance(stepReadyTitle, R.style.Text_Bold_Caption)
        stepReadyTitle.gravity = Gravity.CENTER
        stepReadyTitle.setTextColor(unverifiedTitleColor)
        stepReadyTitle.setAllCaps(true)
        stepReadyTitle.setText(R.string.profile_step_ready)

        stepReadyProgress = View(context)
        stepReadyProgress.id = View.generateViewId()
        stepReadyProgress.layoutParams = ConstraintLayout.LayoutParams(0, progressSize)
        stepReadyProgress.setBackgroundColor(unverifiedTitleColor)

        // Step Verified
        val stepVerifiedParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val stepVerified = FrameLayout(context)
        stepVerified.id = View.generateViewId()
        stepVerified.layoutParams = stepVerifiedParams

        val stepVerifiedUnverifiedParams = FrameLayout.LayoutParams(stepSize, stepSize)
        stepVerifiedUnverifiedParams.gravity = Gravity.CENTER
        stepVerifiedUnverified = TextView(context)
        stepVerifiedUnverified.layoutParams = stepVerifiedUnverifiedParams
        TextViewCompat.setTextAppearance(stepVerifiedUnverified, R.style.Text_Bold_Medium)
        stepVerifiedUnverified.background = unverifiedBackground
        stepVerifiedUnverified.gravity = Gravity.CENTER
        stepVerifiedUnverified.setTextColor(unverifiedTextColor)
        stepVerifiedUnverified.setText(R.string.profile_step_verified_3)

        val stepVerifiedVerifiedParams = FrameLayout.LayoutParams(stepSize, stepSize)
        stepVerifiedVerifiedParams.gravity = Gravity.CENTER
        stepVerifiedVerified = ImageView(context)
        stepVerifiedVerified.layoutParams = stepVerifiedVerifiedParams
        stepVerifiedVerified.background = verifiedBackground
        stepVerifiedVerified.setImageResource(R.drawable.ic_done_success_24dp)
        stepVerifiedVerified.updatePaddingRelative(iconPadding, iconPadding, iconPadding, iconPadding)
        stepVerifiedVerified.isVisible = false

        stepVerified.addView(stepVerifiedUnverified)
        stepVerified.addView(stepVerifiedVerified)

        val stepVerifiedTitleParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        stepVerifiedTitleParams.marginEnd = marginDefault
        stepVerifiedTitleParams.topMargin = marginSmall
        stepVerifiedTitle = TextView(context)
        stepVerifiedTitle.id = View.generateViewId()
        stepVerifiedTitle.layoutParams = stepVerifiedTitleParams
        TextViewCompat.setTextAppearance(stepVerifiedTitle, R.style.Text_Bold_Caption)
        stepVerifiedTitle.gravity = Gravity.CENTER
        stepVerifiedTitle.setTextColor(unverifiedTitleColor)
        stepVerifiedTitle.setAllCaps(true)
        stepVerifiedTitle.setText(R.string.profile_step_verified)

        stepVerifiedProgress = View(context)
        stepVerifiedProgress.id = View.generateViewId()
        stepVerifiedProgress.layoutParams = ConstraintLayout.LayoutParams(0, progressSize)
        stepVerifiedProgress.setBackgroundColor(unverifiedTitleColor)

        addView(stepSignup)
        addView(stepSignupTitle)
        addView(stepReady)
        addView(stepReadyTitle)
        addView(stepReadyProgress)
        addView(stepVerified)
        addView(stepVerifiedTitle)
        addView(stepVerifiedProgress)

        val set = ConstraintSet()
        set.clone(this)

        set.connect(stepSignup.id, END, stepSignupTitle.id, END)
        set.connect(stepSignup.id, START, stepSignupTitle.id, START)
        set.connect(stepSignup.id, TOP, PARENT_ID, TOP)

        set.connect(stepSignupTitle.id, BOTTOM, PARENT_ID, BOTTOM)
        set.connect(stepSignupTitle.id, END, stepReadyTitle.id, START)
        set.connect(stepSignupTitle.id, START, PARENT_ID, START)
        set.connect(stepSignupTitle.id, TOP, stepSignup.id, BOTTOM)
        set.setHorizontalChainStyle(stepSignupTitle.id, CHAIN_SPREAD_INSIDE)
        set.setHorizontalBias(stepSignupTitle.id, 0.5f)

        set.connect(stepReadyProgress.id, BOTTOM, stepSignup.id, BOTTOM)
        set.connect(stepReadyProgress.id, TOP, stepSignup.id, TOP)
        set.connect(stepReadyProgress.id, START, stepSignup.id, END)
        set.connect(stepReadyProgress.id, END, stepReady.id, START)

        set.connect(stepReady.id, END, stepReadyTitle.id, END)
        set.connect(stepReady.id, START, stepReadyTitle.id, START)
        set.connect(stepReady.id, TOP, PARENT_ID, TOP)

        set.connect(stepReadyTitle.id, BOTTOM, PARENT_ID, BOTTOM)
        set.connect(stepReadyTitle.id, END, stepVerifiedTitle.id, START)
        set.connect(stepReadyTitle.id, START, stepSignupTitle.id, END)
        set.connect(stepReadyTitle.id, TOP, stepReady.id, BOTTOM)
        set.setHorizontalBias(stepReadyTitle.id, 0.5f)

        set.connect(stepVerifiedProgress.id, BOTTOM, stepReady.id, BOTTOM)
        set.connect(stepVerifiedProgress.id, TOP, stepReady.id, TOP)
        set.connect(stepVerifiedProgress.id, START, stepReady.id, END)
        set.connect(stepVerifiedProgress.id, END, stepVerified.id, START)

        set.connect(stepVerified.id, END, stepVerifiedTitle.id, END)
        set.connect(stepVerified.id, START, stepVerifiedTitle.id, START)
        set.connect(stepVerified.id, TOP, PARENT_ID, TOP)

        set.connect(stepVerifiedTitle.id, BOTTOM, PARENT_ID, BOTTOM)
        set.connect(stepVerifiedTitle.id, END, PARENT_ID, END)
        set.connect(stepVerifiedTitle.id, START, stepReadyTitle.id, END)
        set.connect(stepVerifiedTitle.id, TOP, stepVerified.id, BOTTOM)
        set.setHorizontalBias(stepVerifiedTitle.id, 0.5f)

        set.applyTo(this)
    }

    fun signUpActive(active: Boolean) {
        stepSignupUnverified.isVisible = !active
        stepSignupVerified.isVisible = active
        stepSignupTitle.setTextColor(when {
            active -> verifiedTitleColor
            else -> unverifiedTitleColor
        })
    }

    fun readyActive(active: Boolean) {
        stepReadyUnverified.isVisible = !active
        stepReadyVerified.isVisible = active
        stepReadyTitle.setTextColor(when {
            active -> verifiedTitleColor
            else -> unverifiedTitleColor
        })
        stepReadyProgress.setBackgroundColor(when {
            active -> verifiedTitleColor
            else -> unverifiedTitleColor
        })
    }

    fun verifiedActive(active: Boolean) {
        stepVerifiedUnverified.isVisible = !active
        stepVerifiedVerified.isVisible = active
        stepVerifiedTitle.setTextColor(when {
            active -> verifiedTitleColor
            else -> unverifiedTitleColor
        })
        stepVerifiedProgress.setBackgroundColor(when {
            active -> verifiedTitleColor
            else -> unverifiedTitleColor
        })
    }
}