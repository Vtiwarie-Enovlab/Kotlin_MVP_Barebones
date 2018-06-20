package com.enovlab.yoop.ui.settings.notifications.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.core.view.doOnLayout

class NotificationItemDecoration(private var space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.set(0, space, 0, 0)
    }
}
