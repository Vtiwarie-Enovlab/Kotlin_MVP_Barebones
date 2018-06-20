package com.enovlab.yoop.ui.widget

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.event.Timeline

class EventTimelineView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
    }

    fun bindTimelines(timelines: List<Timeline>) {
        removeAllViews()

        for (i in 0 until timelines.size) {
            val timeline = timelines[i]
            val timelineView = createTimeline(timeline.title, timeline.description, i == 0)

            addView(timelineView)
            addView(createStepContinue())
            if (i == timelines.size - 1) addView(createStepContinue(true))
        }
    }

    @Suppress("DEPRECATION")
    private fun createTimeline(title: String?, description: String?, isFirst: Boolean): LinearLayout {
        val margin = resources.getDimensionPixelSize(R.dimen.margin_default)
        val titleParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        titleParams.marginStart = margin
        titleParams.marginEnd = margin

        val titleView = AppCompatTextView(context)
        titleView.layoutParams = titleParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleView.setTextAppearance(R.style.Text_Bold_Large_White)
        } else {
            titleView.setTextAppearance(context, R.style.Text_Bold_Large_White)
        }
        titleView.includeFontPadding = false
        titleView.text = title

        val descriptionView = AppCompatTextView(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            descriptionView.setTextAppearance(R.style.Text_Regular_Large_White)
        } else {
            descriptionView.setTextAppearance(context, R.style.Text_Regular_Large_White)
        }
        descriptionView.includeFontPadding = false
        descriptionView.text = description

        val container = LinearLayout(context)
        container.orientation = HORIZONTAL
        container.gravity = Gravity.CENTER_VERTICAL

        val stepMargin = resources.getDimensionPixelSize(R.dimen.timeline_step_margin)
        val containerParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        if (!isFirst) containerParams.topMargin = stepMargin
        containerParams.bottomMargin = stepMargin
        container.layoutParams = containerParams

        container.addView(createStep())
        container.addView(titleView)
        container.addView(descriptionView)

        return container
    }

    private fun createStep(): View {
        val stepSize = resources.getDimensionPixelSize(R.dimen.timeline_step_size)
        val step = View(context)
        step.layoutParams = LinearLayout.LayoutParams(stepSize, stepSize)
        step.background = ContextCompat.getDrawable(context, R.drawable.oval_accent)
        return step
    }

    private fun createStepContinue(isLast: Boolean = false): View {
        val stepSize = resources.getDimensionPixelSize(R.dimen.timeline_step_size)
        val stepContinueWidth = resources.getDimensionPixelSize(R.dimen.timeline_step_continue_width)
        val stepContinueHeight = when {
            isLast == true -> resources.getDimensionPixelSize(R.dimen.timeline_step_continue_last_height)
            else -> resources.getDimensionPixelSize(R.dimen.timeline_step_continue_height)
        }

        val params = LinearLayout.LayoutParams(stepContinueWidth, stepContinueHeight)
        params.marginStart = (stepSize / 2) - (stepContinueWidth / 2)
        val stepContinue = View(context)
        stepContinue.layoutParams = params
        stepContinue.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))

        return stepContinue
    }
}