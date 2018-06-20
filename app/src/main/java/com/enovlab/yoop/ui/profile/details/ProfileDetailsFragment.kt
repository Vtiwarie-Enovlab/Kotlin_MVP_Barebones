package com.enovlab.yoop.ui.profile.details

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.profile.ProfileEditFragment
import com.enovlab.yoop.utils.ext.hideKeyboard
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.fragment_profile_edit_details.*
import kotlinx.android.synthetic.main.layout_profile_edit_change_password.*
import kotlinx.android.synthetic.main.layout_profile_edit_photo.*

class ProfileDetailsFragment : ProfileEditFragment<ProfileDetailsView, ProfileDetailsViewModel>(),
    ProfileDetailsView {

    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = ProfileDetailsViewModel::class.java

    private val changePasswordDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_edit_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_edit_close.setOnClickListener { navigator.navigateBack.go(true to 0L) }

        profile_first_name.textChangeListener(viewModel::firstNameChanged)
        profile_last_name.textChangeListener(viewModel::lastNameChanged)

        container_photo.setOnClickListener { viewModel.capturePhotoClicked() }
        profile_change_password.setOnClickListener { changePasswordDialog.show() }
        profile_log_out.setOnClickListener { viewModel.logOut() }

        profile_edit_save.setOnClickListener {
            viewModel.saveChanges(profile_first_name.getText(), profile_last_name.getText())
            view.hideKeyboard()
        }

        setupChangePasswordDialog()
    }

    private fun setupChangePasswordDialog() {
        changePasswordDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_profile_edit_change_password, null))
        changePasswordDialog.change_password_confirm.setOnClickListener {
            viewModel.changePassword()
        }
        changePasswordDialog.change_password_cancel.setOnClickListener {
            changePasswordDialog.hide()
        }
        changePasswordDialog.change_password_ok.setOnClickListener {
            showPasswordChangeReset()
        }
        changePasswordDialog.setOnDismissListener {
            showPasswordChangeReset()
        }
    }

    override fun showPhoto(url: String?) {
        profile_photo.loadImage(url)
    }

    override fun showVerified(active: Boolean) {
        profile_verified.isVisible = active
    }

    override fun showVerifiedCaption(verified: Boolean) {
        profile_edit_photo_caption.setText(when {
            verified -> R.string.profile_edit_caption_verified
            else -> R.string.profile_edit_caption_not_verified
        })
    }

    override fun showVerifiedTitle(verified: Boolean) {
        profile_edit_photo_title.setText(when {
            verified -> R.string.profile_edit_title_verified
            else -> R.string.profile_edit_title_not_verified
        })
    }

    override fun showVerifiedPhoto(verified: Boolean) {
        profile_photo.borderColor = ContextCompat.getColor(context!!, when {
            verified -> R.color.color_white
            else -> R.color.colorAccent
        })
    }

    override fun showFirstName(firstName: String) {
        profile_first_name.setText(firstName)
    }

    override fun showLastName(lastName: String) {
        profile_last_name.setText(lastName)
    }

    override fun showEmailAddress(email: String) {
        profile_email.setText(email)
        changePasswordDialog.change_password_title.text = getString(R.string.profile_edit_change_password_confirm_title, email)
        changePasswordDialog.change_password_sent_caption.text = getString(R.string.profile_edit_change_password_sent_caption, email)
    }

    override fun showSaveChanges(active: Boolean) {
        profile_edit_save.isVisible = active
    }

    override fun showActionIndicator(active: Boolean) {
        changePasswordDialog.change_password_progress.isVisible = active
    }

    override fun showPasswordChanged(active: Boolean) {
        changePasswordDialog.change_password_group_sent.isVisible = active
    }

    override fun showPasswordChange(active: Boolean) {
        changePasswordDialog.change_password_group_default.isVisible = active
    }

    override fun showPasswordChangeReset() {
        changePasswordDialog.hide()
        showPasswordChange(true)
        showPasswordChanged(false)
    }

    override fun showError(message: String?) {
        super.showError(message)
        showPasswordChangeReset()
    }

    override fun showErrorNoConnection() {
        super.showErrorNoConnection()
        showPasswordChangeReset()
    }

    override fun showEdittingFinished() {
        navigator.navigateBack.go(true to 0L)
    }

    override fun showCapture() {
        navigator.navigateToCapture.go()
    }

    override fun showIntro() {
        navigator.navigateToIntro.go()
    }

    companion object {
        fun newInstance() = ProfileDetailsFragment()
    }
}