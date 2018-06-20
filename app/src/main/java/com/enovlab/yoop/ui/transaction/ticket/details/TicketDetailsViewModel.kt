package com.enovlab.yoop.ui.transaction.ticket.details

import com.enovlab.yoop.ble.UserScanner
import com.enovlab.yoop.data.entity.enums.AssignmentStatus
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.TokenInfo
import com.enovlab.yoop.data.entity.user.UserInfo
import com.enovlab.yoop.data.repository.AssignmentRepository
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem.*
import com.enovlab.yoop.utils.ext.plusAssign
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class TicketDetailsViewModel
@Inject constructor(private val eventsRepository: EventsRepository,
                    private val assignmentRepository: AssignmentRepository) : StateViewModel<TicketDetailsView>() {

    internal lateinit var eventId: String
    internal var ticketId: String? = null

    internal var scannerState = BehaviorSubject.create<UserScanner.State>()

    private lateinit var event: Event
    internal var page = 0
        set(value) {
            if (field != value) {
                field = value
                view?.showPageSelectedIndicator(value)
            }
        }

    private val singleTimeSetup = AtomicBoolean(false)
    private var transferTicketId: String? = null
    private var currentTicketId: String? = null
    private var currentAssignmentId: String? = null
    private var assignedToUser = AtomicBoolean(false)

    override fun start() {
        observeLocalEvent()
        load { eventsRepository.loadEvent(eventId).toCompletable() }
        observePermissions()
    }

    private fun observeLocalEvent() {
        disposables += eventsRepository.observeEventTickets(eventId).subscribe({ event ->
            this.event = event

            view?.showEventName(event.shortName)
            view?.showEventDateLocation(DATE_FORMAT_HEADER.format(event.date), event.locationName)

            val items = createTicketItems(event.tokenInfo, event.assigneeTokenInfo, event.userInfo)

            if (items.isEmpty()) {
                view?.showNoTickets(true)
                view?.showPageIndicatorsActive(false)
            } else {
                view?.showNoTickets(false)
                view?.showPageIndicatorsActive(true)

                if (!singleTimeSetup.getAndSet(true)) {
                    val currentPage = (items.find { it.id == ticketId }?.page
                        ?: 1) - 1 // page index
                    view?.showPageIndicators(items.size, currentPage)
                    if (currentPage > 0) {
                        view?.showPage(currentPage)
                        page = currentPage
                    }
                }
            }

            view?.showTicketItems(items)

        }, { error ->
            Timber.e(error)
        })
    }

    private fun createTicketItems(tokenInfo: List<TokenInfo>?,
                                  assigneeTokenInfo: List<TokenInfo>?,
                                  user: UserInfo?): List<TicketItem> {

        val tokens = mutableListOf<TokenInfo>().apply {
            if (tokenInfo != null && tokenInfo.isNotEmpty()) addAll(tokenInfo)
            if (assigneeTokenInfo != null && assigneeTokenInfo.isNotEmpty()) addAll(assigneeTokenInfo)
        }

        val items = mutableListOf<TicketItem>()
        var currentPage = 0

        // setup user's own token
        // get first token from the assignment
        val userToken = tokens.find { it.selfAssigned == true }
        if (user != null && userToken != null) {
            currentPage = 1
            items.add(createUserTicketItem(userToken, user, currentPage))
            tokens.remove(userToken)
        }

        // setup rest tokens
        tokens.forEachIndexed { i, token ->
            val page = currentPage + i + 1
            val assignment = token.tokenAssignment

            if (assignment == null || assignment.assignmentStatus?.unassigned() == true) {
                items.add(createUnassignedTicketItem(token, page, userToken))
            } else if (assignment.assignmentStatus == AssignmentStatus.PENDING) {
                items.add(createPendingTicketItem(token, page))
            } else {
                items.add(createAssignedTicketItem(token, page))
            }
        }

        return items
    }

    private fun createUserTicketItem(token: TokenInfo, user: UserInfo, page: Int): TicketItem {
        val username = "${user.firstName} ${user.lastName}"

        return if (token.isAssignee == true) {
            val assignment = token.tokenAssignment
            val ownerFirstName = assignment?.ownerFirstName
            when {
                user.photoVerified == true -> AssigneeItem.VerifiedItem(token.id, token.section!!, page, username, assignment!!.id, ownerFirstName!!, user.photo!!)
                user.eventReady == true -> AssigneeItem.EventReadyItem(token.id, token.section!!, page, username, assignment!!.id, ownerFirstName!!, user.photo!!)
                else -> AssigneeItem.NoPhotoItem(token.id, token.section!!, page, username, assignment!!.id, ownerFirstName!!)
            }
        } else when {
            user.photoVerified == true -> UserItem.VerifiedItem(token.id, token.section!!, page, username, user.photo!!)
            user.eventReady == true -> UserItem.EventReadyItem(token.id, token.section!!, page, username, user.photo!!)
            else -> UserItem.NoPhotoItem(token.id, token.section!!, page, username)
        }
    }

    private fun createPendingTicketItem(token: TokenInfo, page: Int): TicketItem.PendingItem {
        val assignment = token.tokenAssignment!!
        return PendingItem(token.id, token.section!!, page, assignment.email!!, DATE_FORMAT.format(assignment.lastActionDate), assignment.id)
    }

    private fun createUnassignedTicketItem(token: TokenInfo, page: Int, userToken: TokenInfo? = null): TicketItem.UnassignedItem {
        val assignment = token.tokenAssignment
        val assignee = if (assignment?.firstName == null) assignment?.email else assignment.firstName
        val selfAssignable = userToken == null || !(token.section == userToken.section && token.row == userToken.row && token.seat == userToken.seat)
        return when (assignment?.assignmentStatus) {
            AssignmentStatus.DECLINED -> UnassignedItem.DeclinedItem(token.id, token.section!!, page, assignee!!, selfAssignable)
            AssignmentStatus.RETURNED -> UnassignedItem.ReturnedItem(token.id, token.section!!, page, assignee!!, selfAssignable)
            AssignmentStatus.REVOKED -> UnassignedItem.RevokedItem(token.id, token.section!!, page, assignee!!, selfAssignable)
            else -> UnassignedItem.NoActionsItem(token.id, token.section!!, page, selfAssignable)
        }
    }

    private fun createAssignedTicketItem(token: TokenInfo, page: Int): TicketItem.AssignedItem {
        val assignment = token.tokenAssignment!!
        val username = "${assignment.firstName} ${assignment.lastName}"
        val acceptDate = DATE_FORMAT.format(assignment.lastActionDate)

        // token accepted
        return when {
            assignment.photoVerified == true -> AssignedItem.VerifiedItem(token.id, token.section!!, page, username, acceptDate, assignment.id, assignment.photo!!)
            assignment.eventReady == true -> AssignedItem.EventReadyItem(token.id, token.section!!, page, username, acceptDate, assignment.id, assignment.photo!!)
            else -> AssignedItem.NoPhotoItem(token.id, token.section!!, page, username, acceptDate, assignment.id)
        }
    }

    internal fun transferTicket(ticketId: String? = currentTicketId) {
        transferTicketId = ticketId
        view?.showTransferFlowDialog(true)
        view?.showMoreDialog(false)
    }

    internal fun transferUseContacts() {
        view?.showTransferFlowContacts(transferTicketId!!)
        view?.showTransferFlowDialog(false)
    }

    internal fun transferInputManually() {
        view?.showTransferFlowManualInput(transferTicketId!!)
        view?.showTransferFlowDialog(false)
    }

    internal fun createIdClicked() {
        when {
            preferences.introSeen -> view?.showProfileCapture()
            else -> view?.showProfileIntro()
        }
    }

    internal fun moreClicked(item: TicketItem, total: Int) {
        currentTicketId = item.id
        when (item) {
            is UserItem -> view?.showMoreOwnerDialog()
            is UnassignedItem -> when {
                item.selfAssignable -> view?.showMoreUnassignedDialog()
                else -> view?.showMoreOwnerDialog()
            }
            is PendingItem -> {
                currentAssignmentId = item.assignmentId
                view?.showMorePendingDialog()
            }
            is AssignedItem -> {
                currentAssignmentId = item.assignmentId
                view?.showMoreAssignedDialog()
            }
            is AssigneeItem -> {
                currentAssignmentId = item.assignmentId
                view?.showMoreAssigneeDialog()
            }
        }
        view?.showMoreDialogSubtitle(page + 1, total)
        view?.showMoreDialog(true)
    }

    internal fun cancelTransfer() {
        action {
            assignmentRepository.cancelAssignment(currentAssignmentId!!)
                .doOnError { view?.showMoreDialog(false) }
                .doOnComplete {
                    view?.showMoreDialog(false)
                    action { eventsRepository.loadEvent(eventId).toCompletable() }
                }
        }
    }

    internal fun resendTicket() {
        action {
            assignmentRepository.resendAssignment(currentAssignmentId!!).toCompletable()
                .doOnError { view?.showMoreDialog(false) }
                .doOnComplete {
                    view?.showMoreDialog(false)
                    action { eventsRepository.loadEvent(eventId).toCompletable() }
                }
        }
    }

    internal fun seeReceipt() {
        view?.showReceipt(eventId, ticketId!!)
        view?.showMoreDialog(false)
    }

    internal fun sendReminder() {
        action {
            assignmentRepository.sendAssignmentReminder(currentAssignmentId!!)
                .doOnError { view?.showMoreDialog(false) }
                .doOnComplete { view?.showMoreDialog(false) }
        }
    }

    internal fun assignToUser() {
        action {
            assignmentRepository.selfAssignToken(eventId, currentTicketId!!)
                .doOnError { view?.showMoreDialog(false) }
                .doOnComplete {
                    view?.showMoreDialog(false)
                    action {
                        eventsRepository.loadEvent(eventId).toCompletable()
                            .doOnComplete { assignedToUser.set(true) }
                    }
                }
        }
    }

    internal fun ticketsChanged() {
        if (assignedToUser.getAndSet(false)) {
            page = 0
            view?.showPage(page)
        }
    }

    private fun observePermissions() {
        scannerState.subscribe { state ->
            when(state) {
                is UserScanner.State.PermissionRequired -> view?.showRequestPermissions()
                is UserScanner.State.BluetoothDisabled -> view?.showBluetoothSettings()
            }
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMM d", Locale.getDefault())
        private val DATE_FORMAT_HEADER = SimpleDateFormat("M/d, ha", Locale.getDefault())
    }
}