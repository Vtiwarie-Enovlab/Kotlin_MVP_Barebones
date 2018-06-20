package com.enovlab.yoop.ui.transaction.confirmation

import android.support.annotation.StringRes
import com.enovlab.yoop.ui.base.state.StateView
import com.google.android.exoplayer2.SimpleExoPlayer

interface TransactionConfirmationView : StateView {
    fun showConfirmationMessage(@StringRes id: Int, date: String?)
    fun showConfirmationMessageGoing(@StringRes id: Int)
    fun showVideoPlayer(player: SimpleExoPlayer)
    fun showPerformerPictureUrl(url: String?)
    fun showUserProfilePictureUrl(url: String?)
    fun showAnimations()
    fun showMyTicketRequested()
    fun showMyTicketSecured()
    fun showLoopGoing()
}