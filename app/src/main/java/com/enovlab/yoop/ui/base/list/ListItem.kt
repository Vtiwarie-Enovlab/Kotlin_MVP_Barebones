package com.enovlab.yoop.ui.base.list

/**
 * Created by Max Toskhoparan on 12/21/2017.
 */

interface ListItem {

    data class Typed(val type: Type) : ListItem

    enum class Type {
        LOADING, DISCOVER, SEARCH, REQUESTED, CONTACTS
    }
}

