package com.enovlab.yoop.api.response

/**
 * Created by Max Toskhoparan on 1/9/2018.
 */
data class ErrorResponse (val uniqueErrorNumber: String?,
                          val message: String?,
                          val errorCode: String?,
                          val httpReturnCode: String?)
