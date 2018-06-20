package com.enovlab.yoop.ui.main.mytickets.inbox.adapter.inbox

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class InboxItemDecoration(private var space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.set(0, 0, 0, space)
    }
}