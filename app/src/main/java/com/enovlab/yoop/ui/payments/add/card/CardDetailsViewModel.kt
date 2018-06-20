package com.enovlab.yoop.ui.payments.add.card

import com.enovlab.yoop.R
import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.widget.CardInputView
import com.enovlab.yoop.utils.RxSchedulers
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject

/**
 * @author vishaan
 */
class CardDetailsViewModel
@Inject constructor(private val userRepository: UserRepository,
                    private val validation: Validation) : StateViewModel<CardDetailsView>() {

    init {
        validation.addValidators(Validator.CARD_NUMBER, Validator.CARD_CVV, Validator.CARD_EXPIRY_DATE)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    internal var cardNumber: String = ""
    internal var expiryDate: String = ""
    internal var cvv: String = ""
    internal var cardNumberValid = false
    internal var cardExpiryDateValid = false
    internal var cardCvvValid = false

    override fun start() {
        initialize()
        validation.check()
        when {
            userRepository.isAuthorized() -> load { userRepository.refreshUser().toCompletable() }
        }
    }

    internal fun initialize() {
        if (cardNumber.isNotBlank()) {
            view?.showFocusExpiryField(true)
        } else if (expiryDate.isNotBlank()) {
            view?.showFocusCVVField(true)
        } else {
            view?.showFocusCardField(true)
        }

        view?.showCardNumberText(this.cardNumber)
        view?.showExpiryDateText(this.expiryDate)
        view?.showCVVText(this.cvv)
    }

    internal fun cardExpiryDateChanged(expiryDate: String) {
        this.expiryDate = expiryDate
        cardExpiryDateValid = validation.validate(Validator.CARD_EXPIRY_DATE, expiryDate)
        view?.showExpireDateValid(cardExpiryDateValid)
    }

    internal fun cardCvvChanged(cvv: String) {
        this.cvv = cvv
        cardCvvValid = validation.validate(Validator.CARD_CVV, cvv)
        view?.showCvvValid(cardCvvValid)
    }

    internal fun cardNumberChanged(cardNumber: String) {
        this.cardNumber = cardNumber
        cardNumberValid = validation.validate(Validator.CARD_NUMBER, cardNumber)

        //add padding if card is shown
        val padding = if (cardNumberValid) R.dimen.padding_small else R.dimen.padding_none
        view?.showCardNumberPadding(padding)

        //show card valid
        view?.showCardValid(cardNumberValid)
        view?.showCard(getCardType(this.cardNumber))
    }

    internal fun resetView(cardNumber: String, expiryDate: String) {
        view?.showCardNumberText(cardNumber)
    }

    private fun getCardType(cardNumber: String): CardInputView.CARD? {
        if (Validator.VISA.validate(cardNumber)) {
            return CardInputView.CARD.VISA
        } else if (Validator.MASTERCARD.validate(cardNumber)) {
            return CardInputView.CARD.MASTERCARD
        } else if (Validator.MAESTRO.validate(cardNumber)) {
            return CardInputView.CARD.MAESTRO
        } else {
            return null
        }
    }
}