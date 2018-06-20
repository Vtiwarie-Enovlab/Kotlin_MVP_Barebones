package com.enovlab.yoop.ui.widget

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet

class NoSelectionTextInputEditText : TextInputEditText {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    public override fun onSelectionChanged(start: Int, end: Int) {
        val text = text
        if (text != null) {
            if (start != text.length || end != text.length) {
                setSelection(text.length, text.length)
                return
            }
        }

        super.onSelectionChanged(start, end)
    }
}