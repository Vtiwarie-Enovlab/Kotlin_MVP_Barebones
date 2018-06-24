package com.enovlab.yoop.ui.base.list

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import kotlinx.android.synthetic.main.item_infinite_loading.*

class LoadingViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.item_infinite_loading) {

    fun bind() {
        loading.isVisible = true
    }
}