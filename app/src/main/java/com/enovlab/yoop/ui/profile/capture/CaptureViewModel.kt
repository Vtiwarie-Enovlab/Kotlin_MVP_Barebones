package com.enovlab.yoop.ui.profile.capture

import android.content.Context
import android.graphics.Bitmap
import com.enovlab.yoop.data.manager.FileManager
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.requiresCameraPermission
import timber.log.Timber
import javax.inject.Inject

class CaptureViewModel
@Inject constructor(private val repository: UserRepository,
                    private val fileManager: FileManager,
                    context: Context) : StateViewModel<CaptureView>() {

    private var photoFile = fileManager.createProfilePhotoFile()
    private var photoCaptured = false
    private var photoCaptureCanceled = false
    private var requiresCameraPermissions = context.requiresCameraPermission()
    private var isRetry = false

    override fun start() {
        when {
            photoCaptureCanceled -> view?.showCameraCaptureCanceled()
            requiresCameraPermissions -> view?.showRequestCameraPermissions()
            !photoCaptured -> view?.showCamera(photoFile)
        }
        view?.showPhotoCaptured(photoCaptured)
        view?.showUseEnabled(photoCaptured && !isError)
    }

    internal fun photoCaptured() {
        isError = false
        isRetry = false
        photoCaptured = true

        defaultViewState()

        view?.showCapturedPhoto(photoFile)
    }

    internal fun photoCaptureCanceled() {
        photoCaptureCanceled = !photoCaptured
    }

    internal fun cameraPermissionsGranted(granted: Boolean) {
        when {
            granted -> {
                requiresCameraPermissions = false
                view?.showCamera(photoFile)
            }
            else -> view?.showCameraPermissionsDenied()
        }
    }

    internal fun retakePhoto() {
        photoFile = fileManager.createProfilePhotoFile()
        view?.showCamera(photoFile)
    }

    internal fun usePhoto(photoBitmap: Bitmap) {
        isRetry = false

        action {
            fileManager.saveProfilePhotoBitmap(photoBitmap)
                .flatMapCompletable { repository.uploadProfilePicture(it) }
                .doOnSubscribe {
                    view?.showPhotoVerificationProgress(true)
                    view?.showPhotoVerificationProgressBorder()
                    view?.showCaptionProgress()
                    view?.showInteractionsEnabled(false)
                }
                .doOnComplete {
                    view?.showPhotoVerificationProgress(false)
                    view?.showCaptionApproved()
                    view?.showPhotoVerificationSuccess(true)
                    view?.showPhotoVerificationSuccessBorder(true)
                    view?.showPhotoVerified()
                }
                .doOnError {
                    view?.showPhotoVerificationProgress(false)
                    view?.showCaptionPositioning()
                    view?.showPhotoVerificationSuccess(false)
                    view?.showPhotoVerificationSuccessBorder(false)
                    view?.showUseEnabled(false)
                    view?.showInteractionsEnabled(true)

                    Timber.e(it)
                }
        }
    }

    internal fun positioningChanged() {
        if (!isRetry && isError) {
            isRetry = true
            defaultViewState()
        }
    }

    private fun defaultViewState() {
        view?.showUseEnabled(true)
        view?.showUseRetry(isRetry)
        view?.showCaptionPositioning()
        view?.showInteractionsEnabled(true)
        view?.showPhotoVerificationDefaultBorder()
    }
}