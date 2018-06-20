package com.enovlab.yoop.ui.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStoreOwner
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.view.View
import com.enovlab.yoop.ui.widget.YoopSnackbar
import com.enovlab.yoop.utils.ext.hideKeyboard
import dagger.android.support.DaggerFragment
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/13/2018.
 */
abstract class BaseFragment<V : BaseView, VM : BaseViewModel<V>> : DaggerFragment(), BaseView {

    protected abstract val viewModelOwner: ViewModelOwner
    protected abstract val vmClass: Class<VM>
    protected lateinit var viewModel: VM
    @Inject internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var snackbar: YoopSnackbar? = null
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

    protected fun showSnackbar(message: String, translate: Boolean = false) {
        if (view != null) showSnackbar(view!!, message, translate = translate)
    }

    protected fun showSnackbar(view: View, message: String, translate: Boolean = false) {
        showSnackbarAction(view, message, translate = translate)
    }

    protected fun showSnackbarAction(message: String,
                                     actionMessage: String? = null, action: (() -> Unit)? = null,
                                     translate: Boolean = false) {
        if (view != null) showSnackbarAction(view!!, message, actionMessage, action, translate)
    }

    protected fun showSnackbarAction(view: View, message: String,
                                     actionMessage: String? = null, action: (() -> Unit)? = null,
                                     translate: Boolean = false) {
        snackbar = YoopSnackbar.make(view, translate)
        snackbar?.text(message)
        if (actionMessage != null && action != null)
            snackbar?.action(actionMessage, action)
        snackbar?.show()
        snackbar?.addCallback(object : BaseTransientBottomBar.BaseCallback<YoopSnackbar>() {
            override fun onDismissed(transientBottomBar: YoopSnackbar?, event: Int) {
                isSnackbarBeingDismissed = false
            }
        })
    }

    protected fun hideSnackbar() {
        if (snackbar?.isShown == true && !isSnackbarBeingDismissed) {
            isSnackbarBeingDismissed = true
            snackbar?.dismiss()
        }
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