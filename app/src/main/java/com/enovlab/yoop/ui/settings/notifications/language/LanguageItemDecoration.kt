package com.enovlab.yoop.ui.settings.notifications.language

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

class LanguageItemDecoration(val divider: Drawable, val padding: Int) : RecyclerView.ItemDecoration() {

    private val bounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.layoutManager == null) {
            return
        }

        drawVertical(c, parent)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view)
        val total = parent.adapter.itemCount - 1

        if (position < total) {
            outRect.set(0, 0, 0, divider.intrinsicHeight)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left = padding
        val right = parent.width - padding
        canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom = bounds.bottom + Math.round(child.translationY)
            val top = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }
}