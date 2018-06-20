package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.ColumnInfo
import com.enovlab.yoop.data.entity.enums.AssignmentStatus
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by mtosk on 3/21/2018.
 */

data class TokenAssignment(

    @ColumnInfo(name = "token_assignment_id")
    var id: String,

    var eventId: String?,

    var inviteeId: String?,

    var inviterId: String?,

    @SerializedName("emailAddress")
    var email: String?,

    var firstName: String?,

    var lastName: String?,

    var ownerFirstName: String?,

    var ownerLastName: String?,

    @SerializedName("assignmentString")
    var confirmationMessage: String?,

    var assignmentStatus: AssignmentStatus?,

    @SerializedName("profilePictureVerificationStatus")
    var photoVerified: Boolean?,

    var eventReady: Boolean?,

    @SerializedName("profilePictureUrl")
    var photo: String?,

    var lastActionDate: Date?
)