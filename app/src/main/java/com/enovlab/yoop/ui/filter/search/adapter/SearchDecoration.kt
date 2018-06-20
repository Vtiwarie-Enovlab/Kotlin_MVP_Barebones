package com.enovlab.yoop.ui.filter.search.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import com.enovlab.yoop.ui.base.list.ListItem

/**
 * Created by Max Toskhoparan on 11/21/2017.
 */

class SearchDecoration : RecyclerView.ItemDecoration {

    var space: Int = 0
    var divider: Drawable? = null
    var padding: Int = 0

    private val bounds = Rect()

    constructor() {
    }

    constructor(divider: Drawable) {
        this.divider = divider
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.layoutManager == null || divider == null) {
            return
        }


        drawVertical(c, parent)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view)

        if (position >= 0 && !isLoadingType(position, parent) && position < parent.adapter.itemCount) {
            outRect.set(0, 0, 0, divider!!.intrinsicHeight)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left = padding
        val right = parent.width - padding
        canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position >= 0 && !isLoadingType(position, parent)) {
                parent.getDecoratedBoundsWithMargins(child, bounds)
                val bottom = bounds.bottom + Math.round(child.translationY)
                val top = bottom - divider!!.intrinsicHeight
                divider!!.setBounds(left, top, right, bottom)
                divider!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    private fun isLoadingType(position: Int, parent: RecyclerView): Boolean {
        return parent.adapter.getItemViewType(position) == ListItem.Type.LOADING.ordinal
    }
}
