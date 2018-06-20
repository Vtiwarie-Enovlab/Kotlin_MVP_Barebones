package com.enovlab.yoop.ui.payments.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.payments.PaymentsFragment
import com.enovlab.yoop.ui.payments.manage.adapter.ManagePaymentsAdapter
import com.enovlab.yoop.ui.payments.manage.adapter.PaymentItem
import com.enovlab.yoop.utils.ext.hideKeyboard
import kotlinx.android.synthetic.main.fragment_manage_payments.*
import kotlinx.android.synthetic.main.layout_manage_payments_app_bar.*

/**
 * @author vishaan
 */
class ManagePaymentsFragment : PaymentsFragment<ManagePaymentsView, ManagePaymentsViewModel>(), ManagePaymentsView {
    override val vmClass = ManagePaymentsViewModel::class.java

    private val paymentsAdapter by lazy { ManagePaymentsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.hideKeyboard()
        back.setOnClickListener { navigator.navigateBack.go(true to 0L) }

        refresh_layout.setOnRefreshListener { viewModel.refresh() }

        //recycler
        paymentsAdapter.newCardListener = { navigator.navigateToAddPayment.go() }
        paymentsAdapter.editCardListener = { navigator.navigateToEditPayment.go(it) }
        manage_payments_list.adapter = paymentsAdapter
    }

    override fun submitList(paymentItems: List<PaymentItem>) {
        paymentsAdapter.submitList(paymentItems)
    }

    override fun showActionIndicator(active: Boolean) {
        refresh_layout.isRefreshing = active
    }

    companion object {
        fun newInstance() = ManagePaymentsFragment()
    }
}