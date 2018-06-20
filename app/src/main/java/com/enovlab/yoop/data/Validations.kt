package com.enovlab.yoop.data

import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/4/2018.
 */

open class Validation @Inject constructor() {

    private val validators = HashMap<Validator, Boolean>()

    val validInput = PublishSubject.create<Boolean>()

    fun addValidators(vararg validators: Validator) {
        validators.forEach { this.validators.put(it, false) }
    }

    fun validate(validator: Validator, toValidate: String): Boolean {
        val valid = validator.validate(toValidate)
        validators.put(validator, valid)
        check()
        return valid
    }

    fun check(value: Boolean = true) {
        validInput.onNext(value && checkValidation())
    }

    fun remove(validator: Validator) {
        validators.remove(validator)
        check()
    }

    fun empty() = validators.isEmpty()

    private fun checkValidation(): Boolean {
        return validators.any { it.value.not() }.not()
    }
}

enum class Validator constructor(val validate: (String) -> Boolean) {
    EMAIL({ ValidationUtil.isEmailValid(it) }),
    PASSWORD({ ValidationUtil.isPasswordValid(it) }),
    PASSWORD_EMPTY({ it.isNotEmpty() }),
    NEW_PASSWORD({ ValidationUtil.isPasswordValid(it) }),
    FIRST_NAME({ ValidationUtil.isTextValid(it) }),
    LAST_NAME({ ValidationUtil.isTextValid(it) }),
    PIN_CODE({ ValidationUtil.isPinCodeValid(it) }),
    COUNTRY({ ValidationUtil.isTextValid(it) }),
    ZIP_CODE({ ValidationUtil.isZipCodeValid(it) }),
    CARD_NUMBER({ ValidationUtil.isCardNumberValid(it) }),
    CARD_HOLDER_NAME({ ValidationUtil.isTextValid(it) }),
    CARD_EXPIRY_DATE({ ValidationUtil.isCardExpiryDateValid(it) }),
    CARD_CVV({ ValidationUtil.isCardCvvValid(it) }),
    VISA( {ValidationUtil.isVisa(it)}),
    MASTERCARD( {ValidationUtil.isMastercard(it)}),
    MAESTRO( {ValidationUtil.isMaestro(it)}),
    PHONE_NUMBER({ ValidationUtil.isPhoneNumberValid(it) })
}

object ValidationUtil {

    const val PIN_LENGTH = 4
    const val ZIP_CODE_LENGTH = 5
    private const val CARD_NUMBER_LENGTH = 16
    private const val CARD_DATE_LENGTH = 5
    private const val CARD_CVV_LENGTH = 3
    private const val PHONE_NUMBER_LENGTH = 10

    private const val EMAIL_ADDRESS_REGEX =
        "([a-z0-9!#$%\\&'*+/=?\\^_`{|}~-]+(?:\\.[a-z0-9!#$%\\&'*+/=?\\^_`{|}" +
            "~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\" +
            "x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-" +
            "z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5" +
            "]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-" +
            "9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
            "-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"


    private const val CARD_EXPIRY_DATE_REGEX = "(?:0[1-9]|1[0-2])/[0-9]{2}"

    private const val VISA = "^4[0-9]{12}(?:[0-9]{3})?$"
    private const val MASTERCARD = "^5[1-5][0-9]{14}$"
    private const val MAESTRO = "^(5018|5020|5038|5612|5813|6304|6759|6761|6762|6763)[0-9]{0,14}$"

    private val CARD_NUMBERS_REGEX = listOf(
            VISA,
            MASTERCARD,
            MAESTRO
    )

    fun isEmailValid(email: String): Boolean {
        return email.toLowerCase().matches(EMAIL_ADDRESS_REGEX.toRegex())
    }

    fun isPasswordValid(password: String): Boolean {
        return has8OrMoreChars(password) && hasNumber(password)
    }

    fun isTextValid(firstName: String): Boolean {
        return firstName.trim().isNotEmpty()
    }

    fun isPinCodeValid(pinCode: String): Boolean {
        return pinCode.length == PIN_LENGTH
    }

    fun isZipCodeValid(zipCode: String): Boolean {
        return zipCode.length == ZIP_CODE_LENGTH
    }

    fun isCardNumberValid(cardNumber: String): Boolean {
        val number = cardNumber.replace(" ", "")
        return number.length >= CARD_NUMBER_LENGTH && CARD_NUMBERS_REGEX.any { number.matches(it.toRegex()) }
    }

    fun isCardExpiryDateValid(expiryDate: String): Boolean {
        return expiryDate.length == CARD_DATE_LENGTH && expiryDate.matches(CARD_EXPIRY_DATE_REGEX.toRegex())
    }

    fun isCardCvvValid(cvv: String): Boolean {
        return cvv.length >= CARD_CVV_LENGTH
    }

    fun isVisa(cardNumber: String): Boolean {
        val number = cardNumber.replace(" ", "")
        return number.length >= CARD_NUMBER_LENGTH && number.matches(VISA.toRegex())
    }

    fun isMastercard(cardNumber: String): Boolean {
        val number = cardNumber.replace(" ", "")
        return number.length >= CARD_NUMBER_LENGTH && number.matches(MASTERCARD.toRegex())
    }

    fun isMaestro(cardNumber: String): Boolean {
        val number = cardNumber.replace(" ", "")
        return number.length >= CARD_NUMBER_LENGTH && number.matches(MAESTRO.toRegex())
    }

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val phone = phoneNumber.replace("[^\\d]".toRegex(), "")
        return phone.length >= PHONE_NUMBER_LENGTH
    }

    fun hasUpperCase(text: String): Boolean {
        return text.matches(".*[A-Z].*".toRegex())
    }

    fun hasLowerCase(text: String): Boolean {
        return text.matches(".*[a-z].*".toRegex())
    }

    fun hasNumber(text: String): Boolean {
        return text.matches(".*\\d.*".toRegex())
    }

    fun has8OrMoreChars(text: String): Boolean {
        return text.length >= 8
    }
}