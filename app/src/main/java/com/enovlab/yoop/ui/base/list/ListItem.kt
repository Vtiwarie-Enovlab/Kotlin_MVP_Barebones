package com.enovlab.yoop.ui.base.list

interface ListItem {

    data class Typed(val type: Type) : ListItem

    enum class Type {
        LOADING, DISCOVER, SEARCH, REQUESTED, CONTACTS
    }
}

