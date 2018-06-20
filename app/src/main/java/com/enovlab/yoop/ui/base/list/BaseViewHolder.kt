package com.enovlab.yoop.ui.base.list

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder(override val containerView: View?)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {

    constructor(parent: ViewGroup, @LayoutRes resId: Int) : this(inflateView(resId, parent))
}