package com.enovlab.yoop.ui.transaction.review

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.event.*
import com.enovlab.yoop.data.entity.user.UserInfo
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.MarketplaceRepository
import com.enovlab.yoop.data.repository.PaymentMethodsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import com.enovlab.yoop.utils.ext.toCompletable
import io.reactivex.Flowable
import timber.log.Timber
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class TransactionReviewViewModel
@Inject constructor(private val eventsRepository: EventsRepository,
                    private val marketplaceRepository: MarketplaceRepository,
                    private val paymentsRepository: PaymentMethodsRepository,
                    private val authRepository: AuthRepository) : StateViewModel<TransactionReviewView>() {

    internal lateinit var id: String
    internal var type: MarketplaceType? = null
    internal var offerGroupId: String? = null
    internal var ticketId: String? = null
    internal var chancesToken: String? = null
    internal var countSelected: Int = 0
    internal var amountEntered: Int = 0
    internal var isUpdate: Boolean = false
    internal var isOverview: Boolean = false
    internal var isFixPayment: Boolean = false
    internal var isClaimTickets: Boolean = false

    internal var autoPay: Boolean = !isClaimTickets

    private val singleData = AtomicBoolean(false)
    private var isPaymentFailed = isFixPayment
    private var offerId: String? = null
    private var retryAttempts = -1

    override fun start() {
        observeData()
        refresh { eventsRepository.loadEvent(id).toCompletable() } //event
    }

    private fun observeData() {
        disposables += eventsRepository.observeEvent(id).subscribe { event ->
            val currency = currencySign(event.currency!!)

            // overview for the Ticket Details
            if (ticketId != null) {
                val tokenInfo = event.tokenInfo?.find { it.id == ticketId }
                val marketplaceAndOfferGroup = findMarketplaceAndOfferGroupByToken(event, tokenInfo)
                if (marketplaceAndOfferGroup != null) {
                    val marketplace = marketplaceAndOfferGroup.first
                    val offerGroup = marketplaceAndOfferGroup.second
                    val offer = offerGroup.offer!!

                    countSelected = offer.numberOfTokens ?: 0
                    amountEntered = offer.amount?.toInt() ?: 0

                    setupHeader(event, offerGroup.description)

                    view?.showHeadlineReceipt()
                    view?.showReviewHeadline(true)

                    setupPricing(offer.tokenPrice!!, offerGroup.reservePrice, currency)
                    view?.showPriceDetails(true)

                    setupPayments()
                    view?.showPaymentDetails(true)
                    view?.showAutoProcess(false)
                    view?.showPaymentEditable(false)

                    view?.showPaymentProcessedDate(DATE_FORMAT_PAYMENT.format(marketplace.endDate))

                    noFooter()
                }

                return@subscribe
            }

            val marketplace = event.marketplaceInfo!!.find { it.type == type }!!
            val offerGroup = marketplace.offerGroups?.find { it.id == offerGroupId }!!

            setupHeader(event, offerGroup.description)

            val hasUserOffer = offerGroup.offer != null

            // build upon existing offer
            if (hasUserOffer && (isClaimTickets || isFixPayment)) {
                val offer = offerGroup.offer!!
                offerId = offer.id
                countSelected = offer.numberOfTokens ?: 0
                amountEntered = offer.amount?.toInt() ?: 0
                retryAttempts = if (retryAttempts == -1) offer.retryAttemptsLeft ?: 0 else retryAttempts

                if (isClaimTickets) {
                    view?.showHeadlineSelected()
                } else if (isFixPayment) {
                    view?.showHeadlineFix()
                }
                view?.showReviewHeadline(true)

                setupPricing(offer.tokenPrice!!, offerGroup.reservePrice, currency)
                view?.showPriceDetails(true)
                view?.showPaymentDetails(true)

                setupPayments()
                view?.showAutoProcessActive(false)

                setupWinnersDate(marketplace)

                noFooter()
                view?.showClaimTickets(true)
                view?.showDeclineTickets(isClaimTickets)
                view?.showClaimTicketsEnabled(!isPaymentFailed && retryAttempts > 0)

                return@subscribe
            }

            // after user has logged in, check if it has already an offer for the particular offer group
            if (!isOverview && !isUpdate && hasUserOffer) {
                view?.showHasOffer(true)
                when (type) {
                    MarketplaceType.DRAW -> view?.showUserHasRequest()
                    MarketplaceType.AUCTION -> view?.showUserHasOffer()
                    else -> { /* nothing */ }
                }

                view?.showReviewHeadline(false)
                view?.showPriceDetails(false)
                view?.showPaymentDetails(false)
                noFooter()

                view?.setNavigationEdit()

                return@subscribe
            }

            when (type) {
                MarketplaceType.DRAW -> view?.showListHeadline()
                MarketplaceType.AUCTION -> view?.showOnSaleHeadline()
                else -> { /* nothing */ }
            }

            view?.showPriceDetails(true)
            view?.showPaymentDetails(true)

            val exceedsLimit = marketplace.userTicketLimitRemaining != null
                && countSelected > marketplace.userTicketLimitRemaining!!

            load {
                pricingSource(event.userInfo, exceedsLimit)
                    .doOnNext { setupPricing(it.tokenPrice, offerGroup.reservePrice, currency) }
                    .toCompletable()
            }

            setupWinnersDate(marketplace)

            // if it is just an overview - hide footer and payments
            if (isOverview) {
                view?.showPaymentCard(false)
                view?.showAutoProcess(false)
                noFooter()

                return@subscribe
            }

            // if empty query is returned - user not authorized
            if (event.userInfo == null) {
                noFooter()
                view?.showNonAuthorized(true)
                view?.showLegalLinks(BuildConfig.LINK_TERMS_AND_CONDITIONS, BuildConfig.LINK_PRIVACY_POLICY)

                return@subscribe
            }

            // if user email is not verified
            if (event.userInfo!!.emailVerified != true) {
                noFooter()
                view?.showNotVerified(true)
                view?.showVerificationEmail(event.userInfo!!.email!!)

                return@subscribe
            }

            setupPayments()

            // check if limits exceeds
            val totalCount = Math.min(offerGroup.numberOfTokens ?: 0, marketplace.limitCount ?: 0)
            val requested = totalCount - (marketplace.userTicketLimitRemaining ?: 0)

            if (exceedsLimit) {
                if (!singleData.getAndSet(true)) {
                    when (type) {
                        MarketplaceType.DRAW -> view?.showLimitExceedsRequestError(requested, totalCount)
                        MarketplaceType.AUCTION -> view?.showLimitExceedsOfferError(requested, totalCount)
                        else -> { /* nothing */ }
                    }
                }
                view?.showTicketLimitExceeds()
                noFooter()

                return@subscribe
            }

            // check if user already made an offer for the event
            when {
                hasUserOffer -> if (requested != offerGroup.offer?.numberOfTokens) {
                    view?.showCautionDialog()
                }
                requested in 1..(totalCount - 1) -> view?.showCautionDialog()
            }

            when (type) {
                MarketplaceType.DRAW -> view?.showCheckoutList(isUpdate)
                MarketplaceType.AUCTION -> view?.showCheckoutOnSale(isUpdate)
                else -> { /* nothing */ }
            }

            noFooter()
            disposables += paymentsRepository.hasPayments().subscribe({ hasPayments ->
                view?.showCheckout(hasPayments)
                view?.showAddPayment(!hasPayments)
            }, { error ->
                Timber.e(error)
            })
        }
    }

    private fun setupHeader(event: Event, section: String?) {
        view?.showEventName(event.shortName)
        view?.showEventDateLocation(DATE_FORMAT_HEADER.format(event.date), event.locationName)
        view?.showDescription(countSelected, section)
    }

    private fun noFooter() {
        view?.showCheckout(false)
        view?.showAddPayment(false)
        view?.showNotVerified(false)
        view?.showNonAuthorized(false)
        view?.showClaimTickets(false)
        view?.showDeclineTickets(false)
    }

    private fun setupPricing(tokenPrice: TokenPrice, reservePrice: Double?, currency: String) {
        val price: Double = when (type) {
            MarketplaceType.AUCTION -> amountEntered.toDouble()
            else -> reservePrice ?: 0.0
        }
        view?.showTicketPrice(currency, price, countSelected)
        view?.showSubtotalPrice(currency, tokenPrice.totalTokenAmount ?: 0.0)
        view?.showTotalPrice(currency, tokenPrice.totalAmount ?: 0.0)

        if (tokenPrice.fee != null) {
            view?.showTicketFee(currency, tokenPrice.fee!!.value ?: 0.0, countSelected)
        } else {
            view?.showTicketFee(currency, tokenPrice.totalFees ?: 0.0, countSelected)
        }
    }

    private fun setupPayments() {
        disposables += paymentsRepository.paymentMethods().subscribe({ payments ->
            if (payments.isNotEmpty()) {
                view?.showPaymentCard(true)
                view?.showAutoProcess(type == MarketplaceType.DRAW)

                val payment = payments.find { it.isDefault == true }!!

                view?.showCardLastNumbers(payment.lastDigits!!)
                view?.showCardPayAttemptFailed(isPaymentFailed)

                when (payment.cardType) {
                    CardType.MC -> view?.showCardTypeMasterCard()
                    CardType.VI -> view?.showCardTypeVisa()
                }

                view?.showAutoProcessEnabled(autoPay)

                view?.showPayments(payments)
            } else {
                view?.showPaymentCard(false)
                view?.showAutoProcess(false)
            }
        }, { error ->
            view?.showPaymentCard(false)
            view?.showAutoProcess(false)
        })

    }

    private fun setupWinnersDate(marketplace: MarketplaceInfo) {
        when (type) {
            MarketplaceType.DRAW -> view?.showChoseListDate(DATE_FORMAT_CHOSE.format(marketplace.endDate))
            MarketplaceType.AUCTION -> view?.showChoseOnSaleDate(DATE_FORMAT_CHOSE.format(marketplace.auctionEndDate))
            else -> { /* nothing */ }
        }
    }

    private fun pricingSource(user: UserInfo?, exceedsLimit: Boolean = false) = when (type) {
        MarketplaceType.DRAW -> when {
            user != null && user.emailVerified == true && !exceedsLimit -> marketplaceRepository.listCheckout(offerGroupId!!, countSelected)
            else -> marketplaceRepository.listPricingDetails(offerGroupId!!, countSelected)
        }
        MarketplaceType.AUCTION -> when {
            user != null && user.emailVerified == true && !exceedsLimit -> marketplaceRepository.onSaleCheckout(offerGroupId!!, countSelected, amountEntered, chancesToken)
            else -> marketplaceRepository.onSalePricingDetails(offerGroupId!!, countSelected, amountEntered, chancesToken)
        }
        else -> Flowable.error(IllegalArgumentException("$type - this marketplace is not supported."))
    }

    internal fun submitRequest() {
        action {
            when (type) {
                MarketplaceType.DRAW -> marketplaceRepository.makeListEntry(offerGroupId!!, countSelected, autoPay)
                else -> marketplaceRepository.makeOnSaleOffer(offerGroupId!!, countSelected, amountEntered, chancesToken)
            }.toCompletable().doOnComplete {
                view?.showConfirmation(id, type!!.name, false)
            }
        }
    }

    internal fun selectPaymentClicked(id: String, payments: List<PaymentMethod>) {
        val defaultPayment = payments.find { it.isDefault == true }
        if (defaultPayment != null && defaultPayment.id == id) {
            view?.showPaymentsDialog(false)
        } else {
            view?.showPaymentSelectingProgress(true)

            disposables += paymentsRepository.setDefaultPaymentMethod(id).subscribe({
                isPaymentFailed = false

                view?.showPaymentSelectingProgress(false)
                view?.showPaymentsDialog(false)

                view?.showClaimTicketsEnabled(!isPaymentFailed && retryAttempts > 0)
            }, { error ->
                view?.showPaymentSelectingProgress(false)
                view?.showPaymentSelectingError()
                view?.showPaymentsDialog(false)
            })
        }
    }

    internal fun claimTickets() {
        view?.showClaimProgress(true)

        disposables += marketplaceRepository.retryPayment(offerId!!)
            .subscribe({
                view?.showClaimSuccess()
                view?.showConfirmation(id, type!!.name, (isClaimTickets || isFixPayment))
            }, { error ->
                view?.showClaimProgress(false)
                if (error !is UnknownHostException) {
                    isPaymentFailed = true
                    view?.showCardPayAttemptFailed(true)
                    view?.showClaimTicketsEnabled(false)
                    view?.showPaymentFailed(--retryAttempts)
                }
            })
    }

    internal fun declineTickets() {
        load {
            marketplaceRepository.declinePayment(offerId!!).doOnComplete {
                view?.showDeclinedPayment()
            }
        }
    }

    internal fun resendLink() {
        load { authRepository.resendVerificationLink() }
    }

    private fun currencySign(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }

    private fun findMarketplaceAndOfferGroupByToken(event: Event,
                                                    tokenInfo: TokenInfo?): Pair<MarketplaceInfo, OfferGroup>? {
        if (event.marketplaceInfo != null) {
            for (marketplace in event.marketplaceInfo!!) {
                for (offerGroup in marketplace.offerGroups!!) {
                    if (offerGroup.offer?.id == tokenInfo?.offer?.id) {
                        return marketplace to offerGroup
                    }
                }
            }
        }

        return null
    }

    companion object {
        private val DATE_FORMAT_HEADER = SimpleDateFormat("M/d, ha", Locale.getDefault())
        private val DATE_FORMAT_CHOSE = SimpleDateFormat("EEEE M/d", Locale.getDefault())
        private val DATE_FORMAT_PAYMENT = SimpleDateFormat("M/d", Locale.getDefault())
    }
}