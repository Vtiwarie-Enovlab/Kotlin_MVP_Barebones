package com.enovlab.yoop.ui.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper

/**
 * Created by Max Toskhoparan on 2/13/2018.
 */
abstract class BaseViewModel<V : BaseView> : ViewModel(), LifecycleObserver {

    protected var view: V? = null

    internal fun attachView(view: V, lifecycle: Lifecycle) {
        this.view = view
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun create() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun start() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun resume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun pause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun stop() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun destroy() {
        this.view = null
    }
}