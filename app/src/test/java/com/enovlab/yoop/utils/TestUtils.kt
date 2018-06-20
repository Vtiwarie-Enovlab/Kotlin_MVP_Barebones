package com.enovlab.yoop.utils

import io.reactivex.schedulers.Schedulers

/**
 * Created by mtosk on 3/9/2018.
 */
object TestUtils {

    fun createTestSchedulers() = RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())
}