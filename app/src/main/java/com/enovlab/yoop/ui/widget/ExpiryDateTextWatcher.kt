package com.enovlab.yoop.ui.widget

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Max Toskhoparan on 11/17/2017.
 */
class ExpiryDateTextWatcher : TextWatcher {

    override fun afterTextChanged(s: Editable) {
        format(s)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    companion object {
        fun format(s: Editable) {
            val forwardSlash = '/'
            if (s.length == 3 && s[2] != forwardSlash) {
                s.insert(2, "" + forwardSlash)
            }
        }


        fun format(s: String): String {
            if(s.isEmpty()) return ""

            val sb = StringBuilder(s.replace("/".toRegex(), ""))
            val forwardSlash = '/'
            if (sb.length >= 3 && sb.get(2) != forwardSlash) {
                sb.insert(2, "" + forwardSlash)
            }
            return sb.toString()
        }
    }
}
