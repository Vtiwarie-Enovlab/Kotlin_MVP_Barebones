package com.enovlab.yoop.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

data class RxSchedulers(val network: Scheduler = Schedulers.io(),
                        val disk: Scheduler = Schedulers.single(),
                        val main: Scheduler = AndroidSchedulers.mainThread())
