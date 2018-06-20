package com.enovlab.yoop.data.entity

/**
 * Created by Max Toskhoparan on 1/31/2018.
 */
data class Country(val name: String, val default: Boolean, val code: String) {
    override fun toString() = name
}