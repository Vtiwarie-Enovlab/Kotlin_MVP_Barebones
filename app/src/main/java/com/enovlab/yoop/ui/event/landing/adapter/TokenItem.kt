package com.enovlab.yoop.ui.event.landing.adapter

sealed class TokenItem(open val id: String,
                       open val sectionName: String) {

    data class UserNoPhotoTokenItem(
        override val id: String,
        override val sectionName: String
    ) : TokenItem(id, sectionName)

    data class UserEventReadyTokenItem(
        override val id: String,
        override val sectionName: String,
        val photoUrl: String
    ) : TokenItem(id, sectionName)

    data class UserVerifiedTokenItem(
        override val id: String,
        override val sectionName: String,
        val photoUrl: String
        ) : TokenItem(id, sectionName)

    data class UnassignedTokenItem(
        override val id: String,
        override val sectionName: String
    ) : TokenItem(id, sectionName)

    data class AssigneePendingTokenItem(
        override val id: String,
        override val sectionName: String,
        val email: String
        ) : TokenItem(id, sectionName)

    data class AssigneeNoPhotoTokenItem(
        override val id: String,
        override val sectionName: String,
        val firstName: String
        ) : TokenItem(id, sectionName)

    data class AssigneeEventReadyTokenItem(
        override val id: String,
        override val sectionName: String,
        val firstName: String,
        val photoUrl: String
        ) : TokenItem(id, sectionName)

    data class AssigneeVerifiedTokenItem(
        override val id: String,
        override val sectionName: String,
        val firstName: String,
        val photoUrl: String
        ) : TokenItem(id, sectionName)
}