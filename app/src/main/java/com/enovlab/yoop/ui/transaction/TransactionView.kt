package com.enovlab.yoop.ui.transaction

import com.enovlab.yoop.ble.UserScanner
import com.enovlab.yoop.ui.base.state.StateView

interface TransactionView : StateView {
    fun showBackgroundImage(url: String?)
    fun showScanStateUpdated(state: UserScanner.State)
}