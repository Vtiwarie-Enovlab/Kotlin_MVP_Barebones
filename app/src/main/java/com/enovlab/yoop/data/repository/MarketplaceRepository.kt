package com.enovlab.yoop.data.repository

import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.request.ListPricingRequest
import com.enovlab.yoop.api.request.OnSalePricingRequest
import com.enovlab.yoop.api.response.ChancesResponse
import com.enovlab.yoop.api.response.OnSaleOfferResponse
import com.enovlab.yoop.api.response.PricingResponse
import com.enovlab.yoop.data.dao.EventDao
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class MarketplaceRepository
@Inject constructor(private val yoopService: YoopService,
                    private val eventDao: EventDao,
                    private val schedulers: RxSchedulers) {

    fun listPricingDetails(id: String, count: Int): Flowable<PricingResponse> {
        return yoopService.getListPricingDetails(ListPricingRequest(id, count))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun onSalePricingDetails(id: String, count: Int, amount: Int, token: String?): Flowable<PricingResponse> {
        return yoopService.getOnSalePricingDetails(OnSalePricingRequest(id, count, amount, token))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun chances(id: String, count: Int): Single<ChancesResponse> {
        return yoopService.getChances(id, count)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun listCheckout(id: String, count: Int): Flowable<PricingResponse> {
        return yoopService.getListCheckout(ListPricingRequest(id, count))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun makeListEntry(id: String, count: Int, autoPay: Boolean = false): Flowable<PricingResponse> {
        return yoopService.makeListEntry(ListPricingRequest(id, count, autoPay))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun onSaleCheckout(id: String, count: Int, amount: Int, token: String?): Flowable<PricingResponse> {
        return yoopService.getOnSaleCheckout(OnSalePricingRequest(id, count, amount, token))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun makeOnSaleOffer(id: String, count: Int, amount: Int, token: String?, autoPay: Boolean = true): Flowable<OnSaleOfferResponse> {
        return yoopService.makeAuctionOffer(OnSalePricingRequest(id, count, amount, token, autoPay))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun deleteListRequest(id: String): Completable {
        return yoopService.deleteListRequest(id)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnComplete { eventDao.deleteOffer(id) }
            .observeOn(schedulers.main)
    }

    fun deleteOnSaleOffer(id: String): Completable {
        return yoopService.deleteOnSaleOffer(id)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnComplete { eventDao.deleteOffer(id) }
            .observeOn(schedulers.main)
    }

    fun archiveOffers(ids: List<String>): Completable {
        return yoopService.archiveOffers(ids)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun declinePayment(offerId: String): Completable {
        return yoopService.declinePayment(offerId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun retryPayment(offerId: String): Completable {
        return yoopService.retryPayment(offerId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }
}