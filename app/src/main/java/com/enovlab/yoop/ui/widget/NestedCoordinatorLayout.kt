package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.util.AttributeSet
import android.view.View

class NestedCoordinatorLayout : CoordinatorLayout, NestedScrollingChild {

    private val childHelper: NestedScrollingChildHelper

    constructor(context: Context) : super(context) {
        childHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        childHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        childHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        /* Enable the scrolling behavior of our own children */
        val tHandled = super.onStartNestedScroll(child, target, nestedScrollAxes)
        /* Enable the scrolling behavior of the parent's other children  */
        return startNestedScroll(nestedScrollAxes) || tHandled
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        /* Enable the scrolling behavior of our own children */
        val tHandled = super.onStartNestedScroll(child, target, axes, type)
        /* Enable the scrolling behavior of the parent's other children  */
        return startNestedScroll(axes) || tHandled
    }

    override fun onStopNestedScroll(target: View) {
        /* Disable the scrolling behavior of our own children */
        super.onStopNestedScroll(target)
        /* Disable the scrolling behavior of the parent's other children  */
        stopNestedScroll()
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        /* Disable the scrolling behavior of our own children */
        super.onStopNestedScroll(target, type)
        /* Disable the scrolling behavior of the parent's other children  */
        stopNestedScroll()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val tConsumed = Array(2) { IntArray(2) }
        super.onNestedPreScroll(target, dx, dy, tConsumed[0])
        dispatchNestedPreScroll(dx, dy, tConsumed[1], null)
        consumed[0] = tConsumed[0][0] + tConsumed[1][0]
        consumed[1] = tConsumed[0][1] + tConsumed[1][1]
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val tConsumed = Array(2) { IntArray(2) }
        super.onNestedPreScroll(target, dx, dy, tConsumed[0], type)
        dispatchNestedPreScroll(dx, dy, tConsumed[1], null)
        consumed[0] = tConsumed[0][0] + tConsumed[1][0]
        consumed[1] = tConsumed[0][1] + tConsumed[1][1]
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        val tHandled = super.onNestedPreFling(target, velocityX, velocityY)
        return dispatchNestedPreFling(velocityX, velocityY) || tHandled
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        val tHandled = super.onNestedFling(target, velocityX, velocityY, consumed)
        return dispatchNestedFling(velocityX, velocityY, consumed) || tHandled
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return childHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        childHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return childHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
    }
}