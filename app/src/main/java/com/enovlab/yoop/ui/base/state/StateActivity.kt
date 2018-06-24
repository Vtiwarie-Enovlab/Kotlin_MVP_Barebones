package com.enovlab.yoop.ui.base.state

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import com.enovlab.yoop.ui.base.BaseActivity

abstract class StateActivity<V : StateView, VM : StateViewModel<V>, N : ViewModel> : BaseActivity<N>(), StateView {

    protected abstract val vmClass: Class<VM>
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(vmClass)
        @Suppress("UNCHECKED_CAST")
        viewModel.attachView(this as V, lifecycle)
    }

    override fun showLoadingIndicator(active: Boolean) {

    }

    override fun showRefreshingIndicator(active: Boolean) {

    }

    override fun showActionIndicator(active: Boolean) {

    }

    override fun showSuccessLoading() {

    }

    override fun showSuccessRefreshing() {

    }

    override fun showSuccessAction() {

    }

    override fun showError(message: String?) {
        val content = findViewById<View>(android.R.id.content)
        if (message != null) {}
//            YoopSnackbar.make(content).text(message).show()
    }

    override fun showErrorNoConnection() {
        val content = findViewById<View>(android.R.id.content)
//        YoopSnackbar.make(content).text(getString(R.string.connection_error)).show()
    }

    override fun showErrorUnauthorized() {

    }

    override fun showInputFieldsEnabled(enabled: Boolean) {

    }
}