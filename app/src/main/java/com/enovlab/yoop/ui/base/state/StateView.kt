package com.enovlab.yoop.ui.base.state

import com.enovlab.yoop.ui.base.BaseView

/**
 * Created by Max Toskhoparan on 2/13/2018.
 */
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