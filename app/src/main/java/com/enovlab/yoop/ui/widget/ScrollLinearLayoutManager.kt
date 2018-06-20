package com.enovlab.yoop.ui.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class ScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    var isScrollEnabled = true

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}