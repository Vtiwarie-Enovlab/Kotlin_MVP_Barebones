package com.enovlab.yoop.ui.transaction.ticket.details.adapter

sealed class TicketItem(open val id: String,
                        open val sectionName: String,
                        open val page: Int) {

    sealed class UserItem(override val id: String,
                          override val sectionName: String,
                          override val page: Int,
                          open val username: String) : TicketItem(id, sectionName, page) {

        data class NoPhotoItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String
        ) : UserItem(id, sectionName, page, username)

        data class EventReadyItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            val photoUrl: String
        ) : UserItem(id, sectionName, page, username)

        data class VerifiedItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            val photoUrl: String
        ) : UserItem(id, sectionName, page, username)
    }

    sealed class UnassignedItem(override val id: String,
                                override val sectionName: String,
                                override val page: Int,
                                open val selfAssignable: Boolean) : TicketItem(id, sectionName, page) {

        data class NoActionsItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val selfAssignable: Boolean
        ) : UnassignedItem(id, sectionName, page, selfAssignable)

        data class DeclinedItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            val assigneeEmail: String,
            override val selfAssignable: Boolean
        ) : UnassignedItem(id, sectionName, page, selfAssignable)

        data class RevokedItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            val assigneeFirstName: String,
            override val selfAssignable: Boolean
        ) : UnassignedItem(id, sectionName, page, selfAssignable)

        data class ReturnedItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            val assigneeFirstName: String,
            override val selfAssignable: Boolean
        ) : UnassignedItem(id, sectionName, page, selfAssignable)
    }

    data class PendingItem(
        override val id: String,
        override val sectionName: String,
        override val page: Int,
        val email: String,
        val assignmentDate: String,
        val assignmentId: String
    ) : TicketItem(id, sectionName, page)

    sealed class AssignedItem(override val id: String,
                              override val sectionName: String,
                              override val page: Int,
                              open val username: String,
                              open val acceptDate: String,
                              open val assignmentId: String) : TicketItem(id, sectionName, page) {

        data class NoPhotoItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            override val acceptDate: String,
            override val assignmentId: String
        ) : AssignedItem(id, sectionName, page, username, acceptDate, assignmentId)

        data class EventReadyItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            override val acceptDate: String,
            override val assignmentId: String,
            val photoUrl: String
        ) : AssignedItem(id, sectionName, page, username, acceptDate, assignmentId)

        data class VerifiedItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            override val acceptDate: String,
            override val assignmentId: String,
            val photoUrl: String
        ) : AssignedItem(id, sectionName, page, username, acceptDate, assignmentId)
    }

    sealed class AssigneeItem(override val id: String,
                              override val sectionName: String,
                              override val page: Int,
                              open val username: String,
                              open val assignmentId: String,
                              open val assignerFirstName: String) : TicketItem(id, sectionName, page) {

        data class NoPhotoItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            override val assignmentId: String,
            override val assignerFirstName: String
        ) : AssigneeItem(id, sectionName, page, username, assignmentId, assignerFirstName)

        data class EventReadyItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            override val assignmentId: String,
            override val assignerFirstName: String,
            val photoUrl: String
        ) : AssigneeItem(id, sectionName, page, username, assignmentId, assignerFirstName)

        data class VerifiedItem(
            override val id: String,
            override val sectionName: String,
            override val page: Int,
            override val username: String,
            override val assignmentId: String,
            override val assignerFirstName: String,
            val photoUrl: String
        ) : AssigneeItem(id, sectionName, page, username, assignmentId, assignerFirstName)
    }
}