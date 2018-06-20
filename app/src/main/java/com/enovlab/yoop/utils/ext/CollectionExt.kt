package com.enovlab.yoop.utils.ext

fun <T> List<T>.findAll(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    forEach({
        if(predicate(it)) {
            list.add(it)
        }
    })
    return list
}