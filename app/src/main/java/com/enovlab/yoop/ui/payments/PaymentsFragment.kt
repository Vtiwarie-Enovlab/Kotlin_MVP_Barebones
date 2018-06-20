package com.enovlab.yoop.ui.payments

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainViewModel

/**
 * @author vishaan
 */
abstract class PaymentsFragment <V : StateView, VM : StateViewModel<V>> : StateFragment<V, VM>() {

    lateinit var navigator: PaymentsNavigator
    override val viewModelOwner = ViewModelOwner.ACTIVITY
    lateinit var hostViewModel: PaymentsMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ViewModelProvider(activity!!, viewModelFactory)
            .get(PaymentsNavigator::class.java)
        hostViewModel = obtainViewModel(PaymentsMainViewModel::class.java)
    }
}