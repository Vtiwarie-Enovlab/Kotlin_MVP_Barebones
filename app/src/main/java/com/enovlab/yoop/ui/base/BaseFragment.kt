package com.enovlab.yoop.ui.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStoreOwner
import android.os.Bundle
import android.view.View
import com.enovlab.yoop.utils.ext.hideKeyboard
import dagger.android.support.DaggerFragment
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

abstract class BaseFragment<V : BaseView, VM : BaseViewModel<V>> : DaggerFragment(), BaseView {

    protected abstract val viewModelOwner: ViewModelOwner
    protected abstract val vmClass: Class<VM>
    protected lateinit var viewModel: VM
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var isSnackbarBeingDismissed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val owner: ViewModelStoreOwner = when (viewModelOwner) {
            ViewModelOwner.ACTIVITY -> activity!!
            ViewModelOwner.FRAGMENT -> this
        }
        viewModel = ViewModelProvider(owner, viewModelFactory).get(vmClass)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.attachView(this as V, lifecycle)
    }

    override fun hideKeyboard() {
        view?.hideKeyboard()
    }

    protected fun <T> PublishSubject<T>.go(param: T) {
        onNext(param)
    }

    protected fun PublishSubject<Unit>.go() {
        onNext(Unit)
    }

    protected fun <T : ViewModel> obtainViewModel(clazz: Class<T>): T {
        return ViewModelProvider(activity!!, viewModelFactory).get(clazz)
    }
}