package com.enovlab.yoop.ui.widget

import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.enovlab.yoop.R
import com.enovlab.yoop.utils.ext.findSuitableParent
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.widget_yoop_snackbar.view.*

/**
 * Created by Max Toskhoparan on 3/1/2018.
 */
class YoopSnackbar
private constructor(parent: ViewGroup, content: View, callback: ContentViewCallback)
    : BaseTransientBottomBar<YoopSnackbar>(parent, content, callback) {

    fun text(text: String): YoopSnackbar {
        view.message.text = text
        return this
    }

    fun action(text: String, listener: () -> Unit): YoopSnackbar {
        view.action.text = text
        view.action.setOnClickListener {
            listener()
            dismiss()
        }
        view.action.isVisible = true
        return this
    }

    companion object {
        fun make(view: View, translate: Boolean = false): YoopSnackbar {
            val parent = view.findSuitableParent()
                ?: throw IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.")
            val content = inflateView<View>(R.layout.widget_yoop_snackbar, parent, false)

            val snackbar = YoopSnackbar(parent, content, ContentViewCallback(view, content, translate))
            snackbar.duration = Snackbar.LENGTH_INDEFINITE
            snackbar.view.background = null
            snackbar.view.updatePadding(left = 0, right = 0)
//            if (parent is CoordinatorLayout) {
//                val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
//                params.bottomMargin = findBottomBarOffset(parent)
//            }
            snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<YoopSnackbar>() {
                override fun onShown(transientBottomBar: YoopSnackbar) {
                    val params = transientBottomBar.view.layoutParams
                    if (params is CoordinatorLayout.LayoutParams) {
                        params.behavior = null
                    }
                }
            })

            content.close.setOnClickListener {
                snackbar.dismiss()
            }

            val elevation = content.resources.getDimension(R.dimen.yoop_snackbar_elevation)
            ViewCompat.setElevation(snackbar.view, elevation)
            ViewCompat.setElevation(content, elevation)

            return snackbar
        }

        private fun findBottomBarOffset(coordinatorLayout: CoordinatorLayout): Int {
            var minOffset = 0
            coordinatorLayout.children.forEach {
                if (it is BottomNavigationView) {
                    if (it.translationY == 0f) minOffset = it.getHeight()
                }
            }
            return minOffset
        }
    }

    private class ContentViewCallback(private val view: View,
                                      private val content: View,
                                      private val translate: Boolean) : BaseTransientBottomBar.ContentViewCallback {

        override fun animateContentIn(delay: Int, duration: Int) {
            if (translate)
                view.animate()
                    .setDuration(duration.toLong())
                    .translationY(-(content.height.toFloat()))
        }

        override fun animateContentOut(delay: Int, duration: Int) {
            if (translate)
                view.animate()
                    .setDuration(duration.toLong())
                    .translationY(0F)
        }
    }
}