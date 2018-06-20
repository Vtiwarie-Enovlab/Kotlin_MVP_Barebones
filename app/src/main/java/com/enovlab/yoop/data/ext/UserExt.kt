package com.enovlab.yoop.data.ext

import com.enovlab.yoop.data.entity.enums.UserAuthenticationState
import com.enovlab.yoop.data.entity.user.User
import com.enovlab.yoop.data.entity.user.UserInfo

fun User.emailVerified() = authState == UserAuthenticationState.EMAIL_VERIFIED

fun User.fullName(): String {
    if (firstName != null && firstName!!.isNotBlank() && lastName != null && lastName!!.isNotBlank()) {
        return firstName!!.capitalize() + " " + lastName!!.capitalize()
    }
    return ""
}

fun User.mapToUserInfo(): UserInfo {
    return UserInfo(firstName, lastName, photo, eventReady, photoVerified, emailVerified(), email)
}