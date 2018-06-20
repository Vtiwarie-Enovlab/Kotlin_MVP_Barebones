package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.enovlab.yoop.utils.ext.translationYForSnackbar

/**
 * Created by Max Toskhoparan on 3/1/2018.
 */
class StatefulFloatingActionButtonBehavior : CoordinatorLayout.Behavior<LinearLayout> {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: LinearLayout, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: LinearLayout, dependency: View): Boolean {
        child.translationY = parent.translationYForSnackbar(child)
        return super.onDependentViewChanged(parent, child, dependency)
    }
}