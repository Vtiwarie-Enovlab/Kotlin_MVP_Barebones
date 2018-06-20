package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import com.enovlab.yoop.R
import com.enovlab.yoop.utils.ext.loadImage
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by Max Toskhoparan on 3/2/2018.
 */
class LoopImageView : FrameLayout {

    private lateinit var picture: ImageView
    private lateinit var loop: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val loopWidth = resources.getDimensionPixelSize(R.dimen.yoop_widget_loop_width)
        val loopHeight = resources.getDimensionPixelSize(R.dimen.yoop_widget_loop_height)
        val pictureSize = resources.getDimensionPixelSize(R.dimen.yoop_widget_loop_picture)

        loop = ImageView(context)
        loop.layoutParams = FrameLayout.LayoutParams(loopWidth, loopHeight)
        loop.setImageResource(R.drawable.yoop_widget_loop)

        picture = CircleImageView(context)
        picture.layoutParams = FrameLayout.LayoutParams(pictureSize, pictureSize).apply { gravity = Gravity.CENTER }
        picture.scaleType = ImageView.ScaleType.CENTER_CROP
        picture.setImageResource(R.drawable.ic_account_loop)

        addView(loop)
        addView(picture)
    }

    fun load(url: String?) {
        picture.loadImage(url)
    }

    fun setImageResource(@DrawableRes imageRes: Int) {
        picture.setImageResource(imageRes)
    }

    fun changeColor(@ColorInt color: Int) {
        loop.setColorFilter(color)
    }
}