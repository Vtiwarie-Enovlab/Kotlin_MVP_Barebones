package com.enovlab.yoop.ui.payments.add.billing

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.entity.Country
import com.enovlab.yoop.data.ext.fullName
import com.enovlab.yoop.data.repository.PaymentMethodsRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * @author vishaan
 */
class BillingDetailsViewModel
@Inject constructor(private val validation: Validation,
                    private val userRepository: UserRepository,
                    private val repository: PaymentMethodsRepository) : StateViewModel<BillingDetailsView>() {

    init {
        validation.addValidators(Validator.CARD_HOLDER_NAME, Validator.ZIP_CODE, Validator.COUNTRY)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    internal var cardHolderName: String = ""
    internal var country: Country = Country(LOCALE.displayCountry, true, LOCALE.country)
    internal var zipCode: String = ""
    internal var cardHolderNameValid = false
    internal var countryValid = false
    internal var zipCodeValid = false
    internal var isDefault = false
    internal val countries = createCountryList()
    internal var shouldValidate = true

    override fun start() {
        initialize()
        view?.showMakeDefaultLabel(true)
        view?.showMakeDefault(true)
        when {
            userRepository.isAuthorized() -> load { userRepository.refreshUser().toCompletable() }
        }
        validation.check()
    }

    internal fun showFocus() {
        if (cardHolderName.isNotBlank()) {
            view?.showZipCodeFocus(true)
        } else {
            view?.showCardHolderNameFocus(true)
        }
    }

    internal fun initialize() {
        //display and validate country by default upon entering screen
        countrySelected(this.country)
        view?.showCountryText(this.country.name)
        view?.showZipCode(this.zipCode)

        setPostalCodeLabel(this.country)

        //display and validate user name by default upon entering screen
        if (userRepository.isAuthorized()) {
            disposables += userRepository.user().subscribe({ user ->
                if (shouldValidate) {
                    this.cardHolderName = user.fullName()
                    cardHolderNameChanged(this.cardHolderName)
                    view?.showCardHolderName(this.cardHolderName)
                    showFocus()
                }
            }, {
                Timber.e(it)
            })
        }
    }

    internal fun cardHolderNameChanged(name: String) {
        this.cardHolderName = name
        cardHolderNameValid = validation.validate(Validator.CARD_HOLDER_NAME, name)
        view?.showCardHolderNameValid(cardHolderNameValid)
    }

    internal fun zipCodeChanged(zip: String) {
        this.zipCode = zip
        zipCodeValid = validation.validate(Validator.ZIP_CODE, zip)
        view?.showZipCodeValid(zipCodeValid)
    }

    internal fun countrySelected(country: Country) {
        this.country = country
        countryValid = validation.validate(Validator.COUNTRY, country.name)
        view?.showCountryText(this.country!!.name)
        view?.showCountryValid(countryValid)
        view?.showCountriesDialog(false)
    }

    internal fun setPostalCodeLabel(country: Country) {
        if (country.code.equals("US", true)) {
            view?.showCodeLabelZip()
        } else {
            view?.showCodeLabelPostal()
        }
    }

    fun addPayment(cardNum: String, holderName: String, cvv: String, month: Int, year: Int, country: String, zip: String, isDefault: Boolean) {
        action {
            repository.createPaymentMethod(cardNum, holderName, cvv, month, year, country, zip, isDefault).toCompletable()
        }
    }

    internal fun cardSetDefaultChanged(isDefault: Boolean) {
        this.isDefault = isDefault
    }

    private fun createCountryList(): List<Country> {
        val countries = mutableSetOf<Country>()
        Locale.getAvailableLocales().forEach {
            val country = it.displayCountry
            if (country.isNotEmpty())
                countries.add(Country(country, it.country == DEFAULT_COUNTRY, it.country))
        }
        return countries.sortedBy { it.name }
    }

    internal fun clearFocus() {
        view?.showClearFocus()
    }

    private fun findCountryCode(countryName: String): String {
        val code = Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
        return if (code != null) code else ""
    }

    companion object {
        private val LOCALE = Locale.getDefault()
        private val DEFAULT_COUNTRY = LOCALE.country
    }
}