package com.enovlab.yoop.ui.payments.add.card

import android.os.Bundle
import android.support.annotation.DimenRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.payments.PaymentsFragment
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainViewModel
import com.enovlab.yoop.ui.widget.CardInputView
import com.enovlab.yoop.ui.widget.CardNumberTextWatcher
import com.enovlab.yoop.ui.widget.ExpiryDateTextWatcher
import kotlinx.android.synthetic.main.fragment_card_details.*

/**
 * @author vishaan
 */
class CardDetailsFragment : PaymentsFragment<CardDetailsView, CardDetailsViewModel>(), CardDetailsView {
    override val vmClass = CardDetailsViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            viewModel.cardNumber = arguments?.getString(ARG_CARD_NUMBER) ?: ""
            viewModel.expiryDate = arguments?.getString(ARG_EXPIRY_DATE) ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //card number listeners
        input_card_number.getInputView().getEditText().addTextChangedListener(CardNumberTextWatcher())
        input_card_number.textChangeListener {
            viewModel.cardNumberChanged(input_card_number.getText())
        }

        //expiration date listeners
        input_card_expiry.textChangeListener { viewModel.cardExpiryDateChanged(input_card_expiry.getText()) }
        input_card_expiry.getInputView().getEditText().addTextChangedListener(ExpiryDateTextWatcher())

        input_card_cvv.textChangeListener { viewModel.cardCvvChanged(input_card_cvv.getText()) }

        //show data if coming from scanner
        showCardNumberText(viewModel.cardNumber)
    }

    override fun showCardValid(valid: Boolean) {
        input_card_number.isValid(valid)
    }

    override fun showCardNumberPadding(@DimenRes id: Int) {
        input_card_number.getInputView().getEditText().compoundDrawablePadding = resources.getDimensionPixelOffset(id)
    }

    override fun showExpireDateValid(valid: Boolean) {
        input_card_expiry.isValid(valid)
    }

    override fun showCvvValid(valid: Boolean) {
        input_card_cvv.isValid(valid)
    }

    override fun showInputValid(valid: Boolean) {
        hostViewModel.inputValidation(valid)
    }

    override fun showCard(cardType: CardInputView.CARD?) {
        input_card_number.showCard(cardType)
    }

    override fun showCardNumberText(cardNumber: String) {
        input_card_number.setText(CardNumberTextWatcher.format(cardNumber))
    }

    override fun showExpiryDateText(expiry: String) {
        input_card_expiry.setText(ExpiryDateTextWatcher.format(expiry))
    }

    override fun showCVVText(cvv: String) {
        input_card_cvv.setText(cvv)
    }

    override fun showEditCardPlaceholder(show: Boolean, lastFour: String) {
    }

    override fun showFocusCardField(show: Boolean) {
        input_card_number.focus()
    }

    override fun showFocusExpiryField(show: Boolean) {
        input_card_expiry.focus()
    }

    override fun showFocusCVVField(show: Boolean) {
        input_card_cvv.focus()
    }

    companion object {
        internal const val ARG_CARD_NUMBER = "ARG_CARD_NUMBER"
        internal const val ARG_EXPIRY_DATE = "ARG_EXPIRY_DATE"

        fun newInstance(scanResult: PaymentsMainViewModel.ScanResult?): CardDetailsFragment {
            return CardDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CARD_NUMBER, scanResult?.cardNumber)
                    putString(ARG_EXPIRY_DATE, scanResult?.expiryDate)
                }
            }
        }
    }
}