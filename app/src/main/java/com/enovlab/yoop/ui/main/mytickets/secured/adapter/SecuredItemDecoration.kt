package com.enovlab.yoop.ui.main.mytickets.secured.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.core.view.doOnLayout

class SecuredItemDecoration(private var space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
//        val position = parent.getChildAdapterPosition(view)
//        val total = parent.adapter.itemCount - 1

        outRect.set(0,  /* if (position == 0) space * 3 else */ 0, 0, space)
    }
}
