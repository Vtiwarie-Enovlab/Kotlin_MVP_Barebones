package com.enovlab.yoop.ui.transaction.details

import android.net.Uri
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.transaction.details.adapter.OfferGroupItem

/**
 * Created by mtosk on 3/5/2018.
 */
interface TransactionDetailsView : StateView {
    fun showEventName(name: String?)
    fun showEventDateLocation(date: String?, location: String?)
    fun showUserPictureUrl(photo: String?)
    fun showPerformerPictureUrl(url: String)
    fun showEventSharing(coverUri: Uri, name: String, location: String, deepLinkUrk: String)
    fun showPriceTypeMinimumAsk()
    fun showPriceTypeList()
    fun showSortingByMinAsk()
    fun showSortingByAverageOffer()
    fun showSorting(active: Boolean)
    fun showSeatMapHeight()
    fun showOfferGroups(offerGroups: List<OfferGroupItem>)
    fun showSeatMap(url: String?, isSvg: Boolean)
    fun showCountSelection(id: String, type: String, offerGroupId: String)
    fun showEditTransaction(id: String, type: String, offerGroupId: String)
}