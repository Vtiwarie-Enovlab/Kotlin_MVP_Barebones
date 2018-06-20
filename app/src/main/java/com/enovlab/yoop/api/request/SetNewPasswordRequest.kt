package com.enovlab.yoop.api.request

/**
 * Created by Max Toskhoparan on 1/8/2018.
 */
data class SetNewPasswordRequest(val oldPassword: String,
                                 val newPassword: String)