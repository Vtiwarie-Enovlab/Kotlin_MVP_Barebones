package com.enovlab.yoop.api.request

/**
 * Created by Max Toskhoparan on 1/8/2018.
 */
data class ResetPasswordRequest(val password: String, val verificationString: String)