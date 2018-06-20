package com.enovlab.yoop.ui.payments.add.card

import android.support.annotation.DimenRes
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.widget.CardInputView

/**
 * @author vishaan
 */
interface CardDetailsView : StateView {
    fun showCardValid(valid: Boolean)
    fun showCardNumberPadding(@DimenRes id: Int)
    fun showExpireDateValid(valid: Boolean)
    fun showCvvValid(valid: Boolean)
    fun showInputValid(valid: Boolean)
    fun showEditCardPlaceholder(show: Boolean, lastFour: String)
    fun showCard(cardType: CardInputView.CARD?)
    fun showCardNumberText(cardNumber: String)
    fun showExpiryDateText(expiry: String)
    fun showCVVText(cvv: String)
    fun showFocusCardField(show: Boolean)
    fun showFocusExpiryField(show: Boolean)
    fun showFocusCVVField(show: Boolean)
}