package com.enovlab.yoop.ui.widget

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Max Toskhoparan on 11/17/2017.
 */
class CardNumberTextWatcher : TextWatcher {

    override fun afterTextChanged(s: Editable) {
        format(s)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    companion object {
        fun format(s: Editable) {
            val space = ' '
            var i = 5
            while (i <= 15) {
                if (s.length == i && s[i - 1] != space) {
                    s.insert(i - 1, "" + space)
                }
                i = i + 5
            }
        }

        fun format(s: String): String {
            if(s.isEmpty()) return ""

            val sb = StringBuilder(s)
            val space = ' '
            var i = 5
            while (i <= 15) {
                if (sb.get(i - 1) != space) {
                    sb.insert(i - 1, "" + space)
                }
                i = i + 5
            }
            return sb.toString()
        }
    }
}
