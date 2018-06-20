package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

/**
 * Created by mtosk on 3/12/2018.
 */
class BottomNavigationBehavior : CoordinatorLayout.Behavior<BottomNavigationView> {

    private var translated = false

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean {
        return dependency is FrameLayout
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                                     child: BottomNavigationView, directTargetChild: View, target: View,
                                     axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        when {
            dy < 0 -> showBottomNavigationView(child)
            dy > 0 -> hideBottomNavigationView(child)
        }
    }

    private fun hideBottomNavigationView(view: BottomNavigationView) {
        if (!translated) {
            translated = true
            view.animate().cancel()

            view.animate()
                .setDuration(DURATION)
                .setInterpolator(DecelerateInterpolator())
                .translationY(view.height.toFloat())
        }
    }

    private fun showBottomNavigationView(view: BottomNavigationView) {
        if (translated) {
            translated = false
            view.animate().cancel()

            view.animate()
                .setDuration(DURATION)
                .setInterpolator(DecelerateInterpolator())
                .translationY(0f)
        }
    }

    companion object {
        private const val DURATION = 270L
    }
}