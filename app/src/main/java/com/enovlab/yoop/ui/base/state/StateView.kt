package com.enovlab.yoop.ui.base.state

import com.enovlab.yoop.ui.base.BaseView

interface StateView : BaseView {
    fun showLoadingIndicator(active: Boolean)
    fun showRefreshingIndicator(active: Boolean)
    fun showActionIndicator(active: Boolean)
    fun showSuccessLoading()
    fun showSuccessRefreshing()
    fun showSuccessAction()
    fun showError(message: String?)
    fun showErrorNoConnection()
    fun showErrorUnauthorized()
    fun showInputFieldsEnabled(enabled: Boolean)
}