package com.enovlab.yoop.ui.transaction.confirmation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.fragment_transaction_confirmation.*

class TransactionConfirmationFragment : TransactionFragment<TransactionConfirmationView, TransactionConfirmationViewModel>(),
        TransactionConfirmationView {

    override val vmClass = TransactionConfirmationViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.type = MarketplaceType.valueOf(arguments?.getString(ARG_MARKETPLACE_TYPE)!!)
        viewModel.isGoing = arguments?.getBoolean(ARG_IS_GOING, false) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAnimations()
    }

    override fun showConfirmationMessage(@StringRes id: Int, date: String?) {
        headline.text = getString(id, date)
    }

    override fun showConfirmationMessageGoing(id: Int) {
        headline.text = getString(id)
    }

    override fun showVideoPlayer(player: SimpleExoPlayer) {
        player_view.player = player
    }

    override fun showPerformerPictureUrl(url: String?) {
        picture_performer.load(url)
    }

    override fun showUserProfilePictureUrl(url: String?) {
        picture_profile.load(url)
    }

    override fun showAnimations() {
        view?.postDelayed({
            val performerProfileX = picture_performer.x
            val userProfileX = picture_profile.x
            picture_performer.x = 0f - picture_performer.width
            picture_profile.x = 0f - picture_profile.width
            loop_line.x = 0f - loop_line.width

            val loopLineAnimation = ObjectAnimator.ofFloat(loop_line, View.X, 0f - loop_line.width, 0f)
            loopLineAnimation.duration = 1000L

            val performerAnimation = ObjectAnimator.ofFloat(picture_performer, View.X, 0f - picture_performer.width, performerProfileX)
            performerAnimation.duration = 1000L

            val profileAnimation = ObjectAnimator.ofFloat(picture_profile, View.X, 0f - picture_profile.width, userProfileX)
            profileAnimation.duration = 1000L

            val textAnimation = ObjectAnimator.ofFloat(headline, View.ALPHA, 0f, 1f)
            textAnimation.duration = 3000L

            val animationSet = AnimatorSet()
            animationSet.play(loopLineAnimation).before(performerAnimation)
            animationSet.play(performerAnimation).before(profileAnimation)
            animationSet.play(profileAnimation).before(textAnimation)
            animationSet.addListener(onEnd = { viewModel.showConfirmationFinished() })
            animationSet.start()
        }, 100)
    }

    override fun showLoopGoing() {
        picture_performer.changeColor(ContextCompat.getColor(context!!, R.color.color_white))
        picture_profile.changeColor(ContextCompat.getColor(context!!, R.color.color_white))
        loop_line.setBackgroundColor(ContextCompat.getColor(context!!, R.color.color_white))
    }

    override fun showMyTicketRequested() {
        navigator.navigateToMyTicketsRequested.go()
    }

    override fun showMyTicketSecured() {
        navigator.navigateToMyTicketsSecured.go()
    }

    companion object {
        private const val ARG_IS_GOING = "ARG_IS_GOING"

        fun newInstance(id: String, type: String, hasPaid: Boolean): TransactionConfirmationFragment {
            return TransactionConfirmationFragment().apply {
                arguments = Bundle(3).apply {
                    putString(ARG_EVENT_ID, id)
                    putString(ARG_MARKETPLACE_TYPE, type)
                    putBoolean(ARG_IS_GOING, hasPaid)
                }
            }
        }
    }
}