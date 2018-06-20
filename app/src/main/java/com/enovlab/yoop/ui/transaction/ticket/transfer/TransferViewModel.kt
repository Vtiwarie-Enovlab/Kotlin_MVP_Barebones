package com.enovlab.yoop.ui.transaction.ticket.transfer

import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.entity.Contact
import com.enovlab.yoop.data.repository.AssignmentRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TransferViewModel
@Inject constructor(private val assignmentRepository: AssignmentRepository,
                    private val userRepository: UserRepository) : StateViewModel<TransferView>() {

    internal lateinit var ticketId: String

    private var transferStates = Stack<TransferState>()

    private var transferTicketEmail: String? = null
    private var transferContacts = listOf<Contact>()

    internal fun initialTransferState(state: TransferState) {
        transferStates.add(state)
    }

    override fun start() {
        disposables += assignmentRepository.tokenInfo(ticketId).subscribe({
            view?.showSection(it.section)
        }, { error ->
            Timber.e(error)
        })
        updateTransferFlow()
    }

    internal fun useContacts() {
        transferStates.add(TransferState.CONTACTS)
        updateTransferFlow()
    }

    internal fun useManualInput() {
        transferStates.add(TransferState.EMAIL)
        transferTicketEmail = null
        updateTransferFlow()
    }

    internal fun contactSelected(item: Contact) {
        transferStates.add(TransferState.EMAIL)
        transferTicketEmail = item.email
        updateTransferFlow()
    }

    internal fun emailVerified(email: String) {
        transferStates.add(TransferState.TRANSFER)
        transferTicketEmail = email
        updateTransferFlow()
    }

    internal fun emailInputChanged(email: String) {
        val valid = Validator.EMAIL.validate(email)
        view?.showEmailValid(valid)
        view?.showProceedEnabled(valid)
    }

    internal fun searchContact(query: String) {
        when {
            query.isNotBlank() -> view?.showContacts(
                transferContacts.filter { it.name.toLowerCase().contains(query) || it.email?.contains(query) == true })
            else -> view?.showContacts(transferContacts)
        }
    }

    internal fun transferConfirmed() {
        action {
            assignmentRepository.assignToken(ticketId, transferTicketEmail!!).toCompletable()
        }
    }

    internal fun transferBack() {
        transferStates.pop()
        when {
            transferStates.empty() -> view?.showTransferClosed()
            else -> updateTransferFlow()
        }
    }

    private fun updateTransferFlow() {
        val state: TransferState? = transferStates.peek()
        view?.showContactsState(state == TransferState.CONTACTS)
        view?.showEmailState(state == TransferState.EMAIL)
        view?.showConfirmState(state == TransferState.TRANSFER)

        view?.showEmail(transferTicketEmail)
        view?.showManualInputKeyboard(state == TransferState.EMAIL)
        if (state == TransferState.TRANSFER) {
            view?.showTransferConfirmEnabled(true)
        }

        if (state == TransferState.CONTACTS) {
            if (transferContacts.isNotEmpty()) {
                view?.showContacts(transferContacts)
            } else {
                view?.showContactsLoading(true)
                disposables += userRepository.contacts().subscribe({ contacts ->
                    transferContacts = contacts
                    view?.showContacts(contacts)
                }, { error ->
                    view?.showContactsLoading(false)
                    Timber.e(error)
                }, {
                    view?.showContactsLoading(false)
                })
            }
        }
    }
}