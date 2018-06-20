package com.enovlab.yoop.ui.transaction.ticket.transfer

import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.data.entity.Contact

interface TransferView : StateView {
    fun showContactsState(active: Boolean)
    fun showEmailState(active: Boolean)
    fun showConfirmState(active: Boolean)
    fun showEmail(email: String?)
    fun showContacts(items: List<Contact>)
    fun showContactsLoading(active: Boolean)
    fun showEmailValid(valid: Boolean)
    fun showProceedEnabled(enabled: Boolean)
    fun showSection(section: String?)
    fun showTransferClosed(delay: Long = 0L)
    fun showManualInputKeyboard(active: Boolean)
    fun showTransferConfirmEnabled(enabled: Boolean)
}