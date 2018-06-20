package com.enovlab.yoop.ui.transaction.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class TransactionOffersItemDecoration(private var space: Int,
                                      val last: Boolean = false) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val s = when {
            last -> space
            else -> {
                val position = parent.getChildAdapterPosition(view)
                val total = parent.adapter.itemCount - 1
                if (position == total) 0 else space
            }
        }

        outRect.set(0, 0, 0, s)
    }
}