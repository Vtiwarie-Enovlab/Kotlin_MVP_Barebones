package com.enovlab.yoop.ui.payments.edit

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.entity.Country
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.ext.fullName
import com.enovlab.yoop.data.repository.PaymentMethodsRepository
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import com.enovlab.yoop.utils.ext.toCompletable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * @author vishaan
 */
class EditPaymentsViewModel
@Inject constructor(private val userRepository: UserRepository,
                    private val validation: Validation,
                    private val repository: PaymentMethodsRepository) : StateViewModel<EditPaymentsView>() {

    init {
        validation.addValidators(Validator.ZIP_CODE, Validator.COUNTRY)
        validation.validInput.subscribe {
            if (isChanged) {
                view?.showSaveButton(it)
            } else {
                view?.showSaveButton(false)
            }
        }
    }

    lateinit internal var paymentMethodId: String
    lateinit internal var paymentMethod: PaymentMethod
    internal var cardHolderName: String = ""
    lateinit internal var country: Country
    internal var zipCode: String = ""
    internal var zipCodeValid = false
    internal var isDefault = false
    internal val countries = createCountryList()
    internal var initialZipCode: String? = null
    internal var initialDefault: Boolean? = null
    internal var initialCountryCode: String? = null
    internal var initialIsDefaultEnabled: Boolean = false
    internal var isChanged: Boolean = false

    override fun start() {
        when {
            userRepository.isAuthorized() -> load { userRepository.refreshUser().toCompletable() }
        }
        observePayments()
    }

    internal fun observePayments() {
        //find the user's payment method to edit. If null, user is entering a new card
        if (userRepository.isAuthorized()) {
            disposables += userRepository.user().subscribe({ user ->

                this.paymentMethod = user.paymentMethods?.find { it.id.equals(paymentMethodId) }!!
                if (paymentMethod != null) {
                    //TODO don't blindly insert name - use the name provided from backend (not implemented by backend). It is for display and should not be editable
                    this.cardHolderName = paymentMethod.cardHolderName ?: user.fullName()

                    //TODO do not get language from locale. It should come from card that was stored in database (not yet implemented)
                    val country = Locale(LOCALE.language, paymentMethod.country)
                    this.country = Country(country.displayCountry, country.country == paymentMethod.country, paymentMethod.country ?: "")
                    this.zipCode = paymentMethod.zipCode ?: ""

                    //display country
                    setPostalCodeLabel(this.country)
                    countrySelected(this.country)
                    view?.showCountryText(this.country.name)

                    //card holder info
                    view?.showCardHolderNameValid(true)
                    view?.showCardHolderName(this.cardHolderName)

                    //card info
                    view?.showCard(paymentMethod.cardType)
                    view?.showLastFour("**** " + paymentMethod.lastDigits)

                    //exipiration date
                    //TODO remove hardcode when able to retrieve expiration date from backend
                    view?.showExpiryDate("" + paymentMethod.expirationMonth.toString().padStart(2, '0') + "/" + paymentMethod.expirationYear.toString().slice(listOf(2, 3)))

                    //zip code
                    if (this.zipCode != null) view?.showZipCode(paymentMethod.zipCode!!)

                    this.initialDefault = paymentMethod.isDefault!!

                    initialZipCode = paymentMethod.zipCode
                    initialDefault = paymentMethod.isDefault
                    initialCountryCode = paymentMethod.country
                    initialIsDefaultEnabled = paymentMethod.isDefault!!.not()

                    view?.showDefaultButtonChecked(initialDefault!!)
                    view?.showDefaultButtonEnabled(initialIsDefaultEnabled)
                    view?.showSaveButton(false)
                    showFocus()
                    validation.check()
                }
            }, {
                Timber.e(it)
            })
        }
    }

    private fun isChanged():Boolean{
        return !this.zipCode.equals(initialZipCode, true) || (this.initialIsDefaultEnabled && this.isDefault != initialDefault) || !this.initialCountryCode.equals(this.country.code, true)
    }

    fun updatePayment() {
        action {
            repository.updatePaymentMethod(paymentMethodId, paymentMethod.cardType?.name!!, country.code, zipCode, isDefault)
                .toCompletable()
                .observeOn(schedulers.main)
                .doOnComplete {
                    view?.hideKeyboard()
                    view?.showCountriesDialog(false)
                    view?.showDeleteConfirmation(false)
                }
        }
    }

    internal fun deleteConfirmClicked() {
        action {
            repository.deletePaymentMethod(paymentMethodId)
                .toCompletable()
                .observeOn(schedulers.main)
                .doOnComplete {
                    view?.hideKeyboard()
                    view?.showCountriesDialog(false)
                    view?.showDeleteConfirmation(false)
                }
        }
    }

    internal fun showFocus() {
        view?.showZipCodeFocus(true)
    }

    internal fun zipCodeChanged(zip: String) {
        this.zipCode = zip
        this.isChanged = isChanged()
        zipCodeValid = validation.validate(Validator.ZIP_CODE, zip)
        view?.showZipCodeValid(zipCodeValid)
    }

    internal fun countrySelected(country: Country) {
        this.country = country
        this.isChanged = isChanged()
        validation.validate(Validator.COUNTRY, country.name)
        view?.showCountryText(this.country!!.name)
        view?.showCountriesDialog(false)
    }

    internal fun cardSetDefaultChanged(isDefault: Boolean) {
        this.isDefault = isDefault
        this.isChanged = isChanged()
        validation.check()
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

    private fun findCountryCode(countryName: String): String {
        val code = Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
        return if (code != null) code else ""
    }

    internal fun setPostalCodeLabel(country: Country) {
        if (country.code.equals("US", true)) {
            view?.showCodeLabelZip()
        } else {
            view?.showCodeLabelPostal()
        }
    }

    companion object {
        private val LOCALE = Locale.getDefault()
        private val DEFAULT_COUNTRY = LOCALE.country
    }
}