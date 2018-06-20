package com.enovlab.yoop.ui.transaction

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel

abstract class TransactionFragment<V : StateView, VM : StateViewModel<V>> : StateFragment<V, VM>() {

    lateinit var navigator: TransactionNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ViewModelProvider(activity!!, viewModelFactory)
            .get(TransactionNavigator::class.java)
    }

    companion object {
        internal const val ARG_EVENT_ID = "ARG_EVENT_ID"
        internal const val ARG_MARKETPLACE_TYPE = "ARG_MARKETPLACE_TYPE"
    }
}