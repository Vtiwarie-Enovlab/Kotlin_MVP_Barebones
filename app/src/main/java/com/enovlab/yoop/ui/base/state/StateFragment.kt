package com.enovlab.yoop.ui.base.state

import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.BaseFragment

abstract class StateFragment<V : StateView, VM : StateViewModel<V>> : BaseFragment<V, VM>(), StateView {

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
//        if (view != null && message != null) showSnackbar(view!!, message)
    }

    override fun showErrorNoConnection() {
//        if (view != null) showSnackbar(view!!, getString(R.string.connection_error))
    }

    override fun showErrorUnauthorized() {

    }

    override fun showInputFieldsEnabled(enabled: Boolean) {

    }
}