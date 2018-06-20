package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v7.widget.LinearSmoothScroller.SNAP_TO_START
import android.graphics.PointF
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager

class HorizontalSmoothScrollLayoutManager(context: Context)
    : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?,
                                        position: Int) {
        val smoothScroller = TopSnappedSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class TopSnappedSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@HorizontalSmoothScrollLayoutManager.computeScrollVectorForPosition(targetPosition)
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
}