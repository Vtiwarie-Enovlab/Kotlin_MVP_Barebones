package com.enovlab.yoop.ui.widget

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet

class DefaultHintEditText : AppCompatEditText {

    private var isWatcherSet = false

    var hint: String? = null
        set(value) {
            field = value
            text.clear()
            text.insert(0, value)
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        hint = DEFAULT_HINT
    }

    override fun onSelectionChanged(start: Int, end: Int) {
        when {
            text.toString() == hint && start != 0 -> setSelection(0, 0)
            text.toString() != hint -> setSelection(text.length, text.length)
            else -> super.onSelectionChanged(start, end)
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused && !isWatcherSet) {
            addTextChangedListener(object : TextWatcher {
                var isDefault = false

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    isDefault = s.toString() == hint && after == hint!!.length
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.isEmpty()) s.insert(0, hint)
                    if (isDefault && s.toString() != DEFAULT_HINT) s.replace(0, s.length, s.first().toString())
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }
            })

            isWatcherSet = true
        }
    }

    companion object {
        private const val DEFAULT_HINT = "0"
    }
}