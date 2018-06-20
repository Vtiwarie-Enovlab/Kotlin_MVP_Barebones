package com.enovlab.yoop.ui.payments.add.billing

import android.support.annotation.StringRes
import com.enovlab.yoop.ui.base.state.StateView

/**
 * @author vishaan
 */
interface BillingDetailsView : StateView {
    fun showMakeDefaultLabel(show: Boolean)
    fun showMakeDefault(show: Boolean)
    fun showInputValid(valid: Boolean)
    fun showCardHolderNameValid(valid: Boolean)
    fun showCountryValid(valid: Boolean)
    fun showZipCodeValid(valid: Boolean)
    fun showCountriesDialog(show: Boolean)
    fun showCountryText(text: String)
    fun showCardHolderName(text: String)
    fun showCountry(text: String)
    fun showZipCode(text: String)
    fun showCardHolderNameFocus(focus: Boolean)
    fun showZipCodeFocus(focus: Boolean)
    fun showClearFocus()
    fun showCodeLabelZip()
    fun showCodeLabelPostal()
}