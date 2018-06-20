package com.enovlab.yoop.ui.transaction.count.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class CountPickerItemDecoration(private var space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view)
        val total = parent.adapter.itemCount - 1

        outRect.set(0, 0, if (position == total) 0 else space, 0)
    }
}
