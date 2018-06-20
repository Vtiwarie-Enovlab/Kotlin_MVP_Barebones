package com.enovlab.yoop.data.entity.user

import android.arch.persistence.room.ColumnInfo

data class UserInfo(
    @ColumnInfo(name = "user_first_name") var firstName: String?,
    @ColumnInfo(name = "user_last_name") var lastName: String?,
    @ColumnInfo(name = "user_photo") var photo: String?,
    @ColumnInfo(name = "user_event_ready") var eventReady: Boolean?,
    @ColumnInfo(name = "user_photo_verified") var photoVerified: Boolean?,
    @ColumnInfo(name = "user_email_verified") var emailVerified: Boolean?,
    @ColumnInfo(name = "user_email") var email: String?
)