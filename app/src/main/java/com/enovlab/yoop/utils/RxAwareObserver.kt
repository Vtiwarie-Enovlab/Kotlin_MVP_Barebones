package com.enovlab.yoop.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.enovlab.yoop.utils.ext.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RxAwareObserver(lifecycle: Lifecycle) : LifecycleObserver {

    private val disposables by lazy { CompositeDisposable() }

    init {
        lifecycle.addObserver(this)
    }

    fun observe(disposable: Disposable) {
        disposables += disposable
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clear() {
        disposables.clear()
    }
}