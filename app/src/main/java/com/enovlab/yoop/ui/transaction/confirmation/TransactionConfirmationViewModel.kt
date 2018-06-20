package com.enovlab.yoop.ui.transaction.confirmation

import android.content.Context
import android.net.Uri
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.EventMediaType
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.event.EventMedia
import com.enovlab.yoop.data.entity.event.Performer
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.VolumeObserver
import com.enovlab.yoop.utils.ext.plusAssign
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionConfirmationViewModel
@Inject constructor(private val repository: EventsRepository,
                    context: Context) : StateViewModel<TransactionConfirmationView>() {

    internal lateinit var id: String
    internal lateinit var type: MarketplaceType
    internal var isGoing: Boolean = false

    private var player: SimpleExoPlayer? = null
    private var isMediaSourceSet = false
    private var isScrolled = false

    private val volumeObserver = VolumeObserver(context)

    init {
        initPlayer(context)
    }

    override fun start() {
        observeEvent()
        action { repository.loadEvent(id).toCompletable() }

        volumeObserver.updateVolume()

        view?.showVideoPlayer(player!!)
        playVideo()
    }

    override fun stop() {
        super.stop()
        pauseVideo()
    }

    override fun destroy() {
        super.destroy()
        releasePlayer()
    }

    private fun observeEvent() {
        disposables += repository.observeEvent(id).subscribe({ event ->
            if (event.marketplaceInfo != null && event.marketplaceInfo!!.isNotEmpty()) {
                val marketplace = event.marketplaceInfo?.find { it.type == type }!!

                //load media
                eventVideoUrl(event.media)?.let { url ->
                    setMediaSource(url)
                }

                performerPictureUrl(event.performers)?.let { url ->
                    view?.showPerformerPictureUrl(url)
                }

                view?.showUserProfilePictureUrl(event.userInfo?.photo)

                //load confirmation messages
                if (isGoing) {
                    view?.showLoopGoing()
                    view?.showConfirmationMessageGoing(R.string.transaction_confirmation_going)
                } else {
                    when {
                        type == MarketplaceType.DRAW -> {
                            view?.showConfirmationMessage(R.string.transaction_confirmation_list, DATE_FORMAT_CHOSE.format(marketplace.endDate))
                        }
                        type == MarketplaceType.AUCTION -> {
                            view?.showConfirmationMessage(R.string.transaction_confirmation_onsale, DATE_FORMAT_CHOSE.format(marketplace.auctionEndDate))
                        }
                        else -> {

                        }
                    }
                }

                playVideo()
            }
        }, { error ->
            Timber.e(error)
        })
    }

    internal fun showConfirmationFinished() {
        when {
            isGoing -> view?.showMyTicketSecured()
            else -> view?.showMyTicketRequested()
        }
    }

    private fun eventVideoUrl(media: List<EventMedia>?): String? {
        if (media == null || media.isEmpty()) return null
        return media.find { it.type == EventMediaType.VIDEO }?.url
    }

    private fun performerPictureUrl(performers: List<Performer>?): String? {
        if (performers == null || performers.isEmpty()) return null
        return performers.first().defaultMedia?.url
    }

    private fun initPlayer(context: Context) {
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(context),
            DefaultTrackSelector(), DefaultLoadControl())
        player?.repeatMode = Player.REPEAT_MODE_ALL
    }

    private fun setMediaSource(url: String) {
        if (!isMediaSourceSet) {
            val mediaSource = ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(USER_AGENT))
                .createMediaSource(Uri.parse(url))
            player?.prepare(mediaSource, false, false)
            isMediaSourceSet = true
        }
    }

    private fun playVideo() {
        if (isMediaSourceSet && !isScrolled && player?.playWhenReady == false)
            player?.playWhenReady = true
    }

    private fun pauseVideo() {
        player?.playWhenReady = false
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    companion object {
        private const val USER_AGENT = "exoplayer-yoop"
        private val DATE_FORMAT_CHOSE = SimpleDateFormat("EEE, M/d", Locale.getDefault())
    }

}