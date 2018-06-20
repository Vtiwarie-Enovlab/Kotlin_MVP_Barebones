package com.enovlab.yoop.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.enovlab.yoop.R

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
class StatefulButton : FrameLayout {

    var state: State? = null
        set(value) {
            field = value
            updateState(value ?: State.ENABLED)
        }

    private lateinit var progressBar: ProgressBar
    private lateinit var icon: AppCompatImageView
    private lateinit var button: AppCompatButton

    private var buttonText: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StatefulButton, defStyleAttr, 0)
        val stateOrdinal = a.getInt(R.styleable.StatefulButton_sb_state, 0)
        val textRes = a.getResourceId(R.styleable.StatefulButton_android_text, 0)
        val textAllCaps = a.getBoolean(R.styleable.StatefulButton_android_textAllCaps, true)
        a.recycle()

        buttonText = resources.getString(textRes)
        init(stateOrdinal, textAllCaps)
    }

    private fun init(stateOrdinal: Int, textAllCaps: Boolean) {
        val progressSize = resources.getDimensionPixelSize(R.dimen.progress_bar_size_fab)
        val progressParams = LayoutParams(progressSize, progressSize)
        progressParams.gravity = Gravity.CENTER

        progressBar = ProgressBar(context)
        progressBar.layoutParams = progressParams
        progressBar.isIndeterminate = true
        progressBar.indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_white))
        progressBar.indeterminateTintMode = PorterDuff.Mode.SRC_IN
        progressBar.elevation = resources.getDimension(R.dimen.progress_bar_elevation_fab)

        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size_default)
        val iconParams = LayoutParams(iconSize, iconSize)
        iconParams.gravity = Gravity.CENTER

        icon = AppCompatImageView(context)
        icon.layoutParams = iconParams
        icon.elevation = resources.getDimension(R.dimen.progress_bar_elevation_fab)

        button = AppCompatButton(ContextThemeWrapper(context, R.style.Button), null, 0)
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        button.setAllCaps(textAllCaps)

        addView(progressBar)
        addView(icon)
        addView(button)

        state = State.values()[stateOrdinal]
    }

    private fun updateState(state: State) {
        icon.setImageResource(state.icon)
        icon.isVisible = state == State.SUCCESS

        progressBar.isVisible = state == State.LOADING

        button.background = ContextCompat.getDrawable(context, state.background)
        button.isEnabled = state == State.ENABLED
        button.text = when (state) {
            State.ENABLED, State.DISABLED -> buttonText
            else -> ""
        }
    }

    fun setText(text: String) {
        buttonText = text
        button.text = when (state) {
            State.ENABLED -> buttonText
            else -> ""
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        button.setOnClickListener(listener)
    }

    enum class State(@DrawableRes val icon: Int, val background: Int) {
        ENABLED(0, R.drawable.background_button_accent),
        LOADING(0, R.drawable.background_button_accent),
        SUCCESS(R.drawable.ic_done_white_32dp, R.drawable.background_button_chances_great),
        DISABLED(0, R.drawable.background_button_disabled),
    }
}