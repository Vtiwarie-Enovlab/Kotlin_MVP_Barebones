package com.enovlab.yoop.ui.widget

import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.enovlab.yoop.R

/**
 * Created by mtosk on 3/5/2018.
 */
open class BluetoothView : LinearLayout {

    private lateinit var title: TextView
    private lateinit var subTitle: TextView
    private lateinit var icon: ImageView

    var state: STATE = STATE.OFF
        set(value) {
            field = value
            updateState(value)
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.InputView, defStyleAttr, 0)
        val stateOrdinal = a.getInt(R.styleable.BluetoothView_state, 0)

        a.recycle()

        init(stateOrdinal)
    }

    private fun init(stateOrdinal: Int) {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER

        icon = ImageView(context)
        title = TextView(context)
        subTitle = TextView(context)

        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bluetooth))

        icon.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        title.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        subTitle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        (icon.layoutParams as MarginLayoutParams).topMargin = resources.getDimensionPixelOffset(R.dimen.margin_default)
        (subTitle.layoutParams as MarginLayoutParams).bottomMargin = resources.getDimensionPixelOffset(R.dimen.margin_sub_large)

        addView(title)
        addView(subTitle)

        state = STATE.values()[stateOrdinal]
    }

    private fun updateState(state: STATE) {
        background = ContextCompat.getDrawable(context, state.backgroundId)
        title.text = if (state.titleId != null) resources.getString(state.titleId, state, subTitle) else ""
        subTitle.text = if (state.subTitleId != null) resources.getString(state.subTitleId, state, subTitle) else ""

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (state.titleStyle != null) {
                title.setTextAppearance(state.titleStyle)
            }
            if (state.subtitleStyle != null) {
                subTitle.setTextAppearance(state.subtitleStyle)
            }
        } else {
            if (state.titleStyle != null) {
                title.setTextAppearance(context, state.titleStyle)
            }
            if (state.subtitleStyle != null) {
                subTitle.setTextAppearance(context, state.subtitleStyle)
            }
        }

        title.setTextColor(ContextCompat.getColor(context, state.color ?: R.color.colorPrimary))
        subTitle.setTextColor(ContextCompat.getColor(context, state.color ?: R.color.colorPrimary))
    }

    //TODO set data for icon (black and white)
    //TODO use CORRECT styles and colors
    enum class STATE(@DrawableRes val backgroundId: Int,
                     @StringRes val titleId: Int?,
                     @StringRes val subTitleId: Int?,
                     @StyleRes val titleStyle: Int?,
                     @StyleRes val subtitleStyle: Int?,
                     @ColorRes val color: Int?) {
        OFF(R.drawable.background_bluetooth_off, R.string.ticket_details_bluetooth_show_entry, R.string.ticket_details_bluetooth_must_be_on, R.style.Text_Bold_Large, R.style.Text_Bold_Large, R.color.colorPrimary),
        OFF_ON_DAY(R.drawable.background_bluetooth_off, R.string.ticket_details_bluetooth_show_entry, R.string.ticket_details_bluetooth_ready, R.style.Text_Bold_Large, R.style.Text_Bold_Large, R.color.colorPrimary),
        OFF_READY(R.drawable.background_bluetooth_off_ready, R.string.ticket_details_bluetooth_turn_on_for_entry, null, R.style.Text_Bold_Large, R.style.Text_Bold_Large, R.color.colorPrimary),
        ON_READY(R.drawable.background_bluetooth_on, R.string.ticket_details_bluetooth_ready_for_checkin, null, R.style.Text_Bold_Large, R.style.Text_Bold_Large, R.color.colorPrimary),
        ON_CONTACT_INIT(R.drawable.background_bluetooth_on, R.string.ticket_details_bluetooth_contact, null, R.style.Text_Bold_Large, R.style.Text_Bold_Large, R.color.colorPrimary),
        ON_CHECKED_IN(R.drawable.background_bluetooth_on, R.string.ticket_details_bluetooth_checked_in, null, R.style.Text_Bold_Large, R.style.Text_Bold_Large, R.color.colorPrimary)
    }
}