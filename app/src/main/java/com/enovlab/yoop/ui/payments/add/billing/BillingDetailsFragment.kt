package com.enovlab.yoop.ui.payments.add.billing

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.payments.PaymentsFragment
import com.enovlab.yoop.ui.payments.add.billing.adapter.CountriesAdapter
import kotlinx.android.synthetic.main.fragment_payments_main.*
import kotlinx.android.synthetic.main.layout_countries_list.*
import kotlinx.android.synthetic.main.layout_countries_list.view.*
import kotlinx.android.synthetic.main.layout_payment_app_bar.view.*
import kotlinx.android.synthetic.main.layout_payments_billing_template.*

/**
 * @author vishaan
 */
class BillingDetailsFragment : PaymentsFragment<BillingDetailsView, BillingDetailsViewModel>(), BillingDetailsView {
    override val vmClass = BillingDetailsViewModel::class.java

    private val countriesDialog by lazy { BottomSheetDialog(context!!) }
    private val adapter by lazy { CountriesAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.view?.back?.isVisible = true

        viewModel.cardSetDefaultChanged(parentFragment?.switch_default?.isChecked ?: false)

        //card holder field
        input_card_holder.textChangeListener { viewModel.cardHolderNameChanged(input_card_holder.getText()) }

        //zip code field
        input_zip.textChangeListener { viewModel.zipCodeChanged(input_zip.getText()) }
        parentFragment?.switch_default?.setOnCheckedChangeListener { v, checked ->
            viewModel.cardSetDefaultChanged(parentFragment?.switch_default?.isChecked ?: false)
        }

        //countries dialog
        val countriesSheet = LayoutInflater.from(context!!)
            .inflate(R.layout.layout_countries_list, null)
        countriesDialog.setContentView(countriesSheet)

        input_country.setOnClickListener {
            showCountriesDialog(true)
        }
        countriesSheet.countries_list.adapter = adapter
        (countriesSheet.countries_list.adapter as CountriesAdapter).listener = { viewModel.countrySelected(it) }
        adapter.submitList(viewModel.countries)
    }

    override fun showMakeDefaultLabel(show: Boolean) {
        parentFragment?.switch_default_label?.isVisible = show
    }

    override fun showMakeDefault(show: Boolean) {
        parentFragment?.switch_default?.isVisible = show
    }

    override fun showInputValid(valid: Boolean) {
        hostViewModel.inputValidation(valid)
    }

    override fun showCardHolderNameValid(valid: Boolean) {
        input_card_holder.isValid(valid)
    }

    override fun showCountryValid(valid: Boolean) {
    }

    override fun showZipCodeValid(valid: Boolean) {
        input_zip.isValid(valid)
    }

    override fun showCountriesDialog(show: Boolean) {
        countriesDialog.countries_list.isVisible = show
        if (show) countriesDialog.show() else countriesDialog.hide()
    }

    override fun showCountryText(text: String) {
        input_country.setText(text)
    }

    override fun showCardHolderName(text: String) {
        input_card_holder.setText(text)
    }

    override fun showCountry(text: String) {
        input_country.setText(text)
    }

    override fun showZipCode(text: String) {
        input_zip.setText(text)
    }

    override fun showCardHolderNameFocus(focus: Boolean) {
        input_card_holder.focus()
    }

    override fun showZipCodeFocus(focus: Boolean) {
        input_zip.focus()
    }

    override fun showActionIndicator(active: Boolean) {
        hostViewModel.loadingStarted(active)
    }

    override fun showSuccessAction() {
        viewModel.shouldValidate = false
        hostViewModel.success()
    }

    override fun showClearFocus() {
        input_zip.clearFocus()
        hideKeyboard()
    }

    override fun showCodeLabelZip() {
        input_zip.setHint(R.string.payments_zip_postal)
    }

    override fun showCodeLabelPostal() {
        input_zip.setHint(R.string.payments_billing_postal)
    }

    override fun showError(message: String?) {
        if (message != null) showSnackbar(parentFragment?.container_step as View, message, false)
    }

    companion object {
        fun newInstance() = BillingDetailsFragment()
    }
}