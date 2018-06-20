package com.enovlab.yoop.ui.profile.capture

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.profile.ProfileEditFragment
import com.enovlab.yoop.ui.widget.StatefulButton
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImageFromFile
import kotlinx.android.synthetic.main.fragment_profile_edit_capture.*
import kotlinx.android.synthetic.main.layout_profile_capture_app_bar.*
import java.io.File

class CaptureFragment : ProfileEditFragment<CaptureView, CaptureViewModel>(), CaptureView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = CaptureViewModel::class.java

    private val helpDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_edit_capture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_capture_close.setOnClickListener { navigator.navigateBack.go(false to 0L) }
        profile_capture_help.setOnClickListener { helpDialog.show() }

        capture_retake.setOnClickListener {
            viewModel.retakePhoto()
        }
        capture_use.setOnClickListener {
            viewModel.usePhoto(cropBitmap())
        }

        captured_overlay_background.setOverlayColor(R.color.color_black_alpha_60)
        captured_overlay_background.doOnLayout {
            captured_photo.layoutParams = captured_photo.layoutParams.apply {
                val circleSize = captured_overlay_background.circleSize()
                height = circleSize
                width = circleSize
            }
        }

        captured_photo.setOnMatrixChangeListener {
            viewModel.positioningChanged()
            hideSnackbar()
        }
        captured_photo.setOnScaleChangeListener { _, _, _ ->
            viewModel.positioningChanged()
            hideSnackbar()
        }

        helpDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_profile_capture_help, null))
    }

    override fun showRequestCameraPermissions() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), RC_CAMERA_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RC_CAMERA_PERMISSION) {
            viewModel.cameraPermissionsGranted((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED } ))
        }
    }

    override fun showCameraPermissionsDenied() {
        navigator.navigateBack.go(false to 0L)
    }

    override fun showCamera(photoFile: File) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            val photoURI = FileProvider.getUriForFile(context!!, AUTHORITY, photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            startActivityForResult(intent, RC_CAPTURE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CAPTURE_PHOTO) {
            when (resultCode) {
                Activity.RESULT_OK -> viewModel.photoCaptured()
                else -> viewModel.photoCaptureCanceled()
            }
        }
    }

    override fun showCapturedPhoto(photoFile: File) {
        captured_photo.loadImageFromFile(photoFile)
    }

    override fun showCameraCaptureCanceled() {
        navigator.navigateBack.go(false to 100L)
    }

    override fun showPhotoCaptured(active: Boolean) {
        app_bar.isVisible = active
        captured_photo.isVisible = active
        captured_overlay_background.isVisible = active
        profile_capture_caption.isVisible = active
        capture_retake.isVisible = active
        capture_use.isVisible = active
    }

    override fun showPhotoVerificationProgress(active: Boolean) {
        capture_use.state = when {
            active -> StatefulButton.State.LOADING
            else -> StatefulButton.State.ENABLED
        }
    }

    override fun showPhotoVerificationSuccess(success: Boolean) {
        capture_use.state = when {
            success -> StatefulButton.State.SUCCESS
            else -> StatefulButton.State.ENABLED
        }
    }

    override fun showPhotoVerificationSuccessBorder(success: Boolean) {
        captured_overlay_background.setBorderColor(when {
            success -> R.color.color_on_sale_chance_great
            else -> R.color.color_on_sale_chance_wont
        })
    }

    override fun showPhotoVerificationProgressBorder() {
        captured_overlay_background.setBorderColor(R.color.color_timeline_background)
    }

    override fun showPhotoVerificationDefaultBorder() {
        captured_overlay_background.setBorderColor(R.color.colorAccent)
    }

    override fun showCaptionPositioning() {
        profile_capture_caption.setText(R.string.profile_edit_capture_position)
    }

    override fun showCaptionProgress() {
        profile_capture_caption.setText(R.string.profile_edit_capture_progress)
    }

    override fun showCaptionApproved() {
        profile_capture_caption.setText(R.string.profile_edit_capture_approved)
    }

    override fun showUseEnabled(enabled: Boolean) {
        capture_use.isEnabled = enabled
        capture_use.alpha = if (enabled) 1f else 0.6f
    }

    override fun showUseRetry(retry: Boolean) {
        capture_use.setText(getString(when {
            retry -> R.string.profile_edit_capture_try_again
            else -> R.string.profile_edit_capture_use
        }))
    }

    override fun showInteractionsEnabled(enabled: Boolean) {
        profile_capture_close.isEnabled = enabled
        profile_capture_help.isEnabled = enabled
        capture_retake.isEnabled = enabled
        captured_photo.isEnabled = enabled

        profile_capture_close.alpha = if (enabled) 1f else 0.6f
        profile_capture_help.alpha = if (enabled) 1f else 0.6f
        capture_retake.alpha = if (enabled) 1f else 0.6f
        captured_overlay_background.showOverlay(!enabled)
    }

    override fun showPhotoVerified() {
        navigator.navigateBack.go(false to 1000L)
    }

    private fun cropBitmap(): Bitmap {
        val matrix = Matrix()
        captured_photo.getDisplayMatrix(matrix)

        val values = FloatArray(9)
        matrix.getValues(values)

        val scale = captured_photo.scale
        val transitionX = Math.abs(values[Matrix.MTRANS_X])
        val transitionY = Math.abs(values[Matrix.MTRANS_Y])

        val cropX = (transitionX / scale).toInt()
        val cropY = (transitionY / scale).toInt()
        val newWidth = (captured_photo.width / scale).toInt()
        val newHeight = (captured_photo.height / scale).toInt()

        return Bitmap.createBitmap((captured_photo.drawable as BitmapDrawable).bitmap,
            cropX, cropY, newWidth, newHeight, null, false)
    }

    companion object {
        fun newInstance() = CaptureFragment()

        private const val AUTHORITY = "com.enovlab.yoop.fileprovider"
        private const val RC_CAPTURE_PHOTO = 54
        private const val RC_CAMERA_PERMISSION = 53
    }
}