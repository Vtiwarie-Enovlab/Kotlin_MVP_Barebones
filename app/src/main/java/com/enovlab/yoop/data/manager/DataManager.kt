package com.enovlab.yoop.data.manager

import io.reactivex.Completable

/**
 * Created by Max Toskhoparan on 1/9/2018.
 */
interface DataManager {
    fun clear(clearUser: Boolean = true): Completable
}