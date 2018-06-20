package com.enovlab.yoop.ui.payments.edit

import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.ui.base.state.StateView

/**
 * @author vishaan
 */
interface EditPaymentsView : StateView {
    fun showDeleteConfirmation(show: Boolean)
    fun showFailedDeleteMessage()
    fun showCard(cardType: CardType?)
    fun showLastFour(lastFour: String?)
    fun showExpiryDate(expiryDate: String?)
    fun showDefaultButtonEnabled(enabled: Boolean)
    fun showDefaultButtonChecked(On: Boolean)
    fun showMakeDefaultLabel(show: Boolean)
    fun showMakeDefault(show: Boolean)
    fun showCardHolderNameValid(valid: Boolean)
    fun showZipCodeValid(valid: Boolean)
    fun showCountriesDialog(show: Boolean)
    fun showCountryText(text: String)
    fun showCardHolderName(text: String)
    fun showCountry(text: String)
    fun showZipCode(text: String)
    fun showCardHolderNameFocus(focus: Boolean)
    fun showZipCodeFocus(focus: Boolean)
    fun showSaveButton(show: Boolean)
    fun showCodeLabelZip()
    fun showCodeLabelPostal()
}