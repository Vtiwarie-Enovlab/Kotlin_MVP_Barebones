package com.enovlab.yoop.ui.profile.capture

import com.enovlab.yoop.ui.base.state.StateView
import java.io.File

interface CaptureView : StateView {
    fun showCamera(photoFile: File)
    fun showCapturedPhoto(photoFile: File)
    fun showPhotoCaptured(active: Boolean)
    fun showRequestCameraPermissions()
    fun showCameraPermissionsDenied()
    fun showCameraCaptureCanceled()
    fun showPhotoVerificationProgress(active: Boolean)
    fun showPhotoVerificationSuccess(success: Boolean)
    fun showPhotoVerificationSuccessBorder(success: Boolean)
    fun showPhotoVerificationProgressBorder()
    fun showPhotoVerificationDefaultBorder()
    fun showCaptionProgress()
    fun showCaptionApproved()
    fun showCaptionPositioning()
    fun showUseEnabled(enabled: Boolean)
    fun showInteractionsEnabled(enabled: Boolean)
    fun showPhotoVerified()
    fun showUseRetry(retry: Boolean)
}