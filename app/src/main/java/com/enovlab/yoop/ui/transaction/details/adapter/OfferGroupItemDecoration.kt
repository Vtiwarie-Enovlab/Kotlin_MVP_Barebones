package com.enovlab.yoop.ui.transaction.details.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.core.view.doOnLayout

class OfferGroupItemDecoration(private var space: Int) : RecyclerView.ItemDecoration() {

    var itemsTotalHeight = 0

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.set(0, 0, 0, space)

        view.doOnLayout {
            itemsTotalHeight += it.height + space
        }
    }
}
