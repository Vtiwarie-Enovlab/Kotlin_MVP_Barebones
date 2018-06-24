package com.enovlab.yoop.api.response

data class ErrorResponse (val uniqueErrorNumber: String?,
                          val message: String?,
                          val errorCode: String?,
                          val httpReturnCode: String?)
