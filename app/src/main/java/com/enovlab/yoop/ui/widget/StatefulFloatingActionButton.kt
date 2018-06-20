package com.enovlab.yoop.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.enovlab.yoop.R

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
class StatefulFloatingActionButton : FrameLayout {

    var state: State? = null
        set(value) {
            field = value
            updateState(value ?: State.DISABLED)
        }

    private lateinit var progressBar: ProgressBar
    private lateinit var fab: FloatingActionButton

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StatefulFloatingActionButton, defStyleAttr, 0)
        val stateOrdinal = a.getInt(R.styleable.StatefulFloatingActionButton_sfab_state, 1)
        a.recycle()

        init(stateOrdinal)
    }

    private fun init(stateOrdinal: Int) {
        val progressSize = resources.getDimensionPixelSize(R.dimen.progress_bar_size_fab)
        val progressParams = LayoutParams(progressSize, progressSize)
        progressParams.gravity = Gravity.CENTER

        progressBar = ProgressBar(context)
        progressBar.layoutParams = progressParams
        progressBar.isIndeterminate = true
        progressBar.indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_white))
        progressBar.indeterminateTintMode = PorterDuff.Mode.SRC_IN
        progressBar.elevation = resources.getDimension(R.dimen.progress_bar_elevation_fab)

        val fabParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        fab = FloatingActionButton(context)
        fab.layoutParams = fabParams
        fab.scaleType = ImageView.ScaleType.CENTER
        fab.customSize = resources.getDimensionPixelSize(R.dimen.fab_size)
        fab.size = FloatingActionButton.SIZE_NORMAL

        addView(progressBar)
        addView(fab)

        state = State.values()[stateOrdinal]
    }

    private fun updateState(state: State) {
        fab.setImageResource(state.icon)
        fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, state.color))
        fab.isEnabled = state == State.ENABLED || state == State.DISABLED
        progressBar.isVisible = state == State.LOADING
    }

    override fun setOnClickListener(l: OnClickListener?) {
        fab.setOnClickListener(l)
    }

    enum class State(@DrawableRes val icon: Int, @ColorRes val color: Int) {
        ENABLED(R.drawable.ic_arrow_right_white_42dp, R.color.colorAccent),
        DISABLED(R.drawable.ic_arrow_right_white_opacity_50_42dp, R.color.color_button_disabled),
        DISABLED_FULL(R.drawable.ic_arrow_right_white_opacity_50_42dp, R.color.color_button_disabled),
        LOADING(0, R.color.colorAccent),
        SUCCESS(R.drawable.ic_done_white_32dp, R.color.color_on_sale_chance_great)
    }
}