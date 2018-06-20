package com.enovlab.yoop.ui.filter

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel

/**
 * Created by mtosk on 3/14/2018.
 */
abstract class FilterFragment<V : StateView, VM : StateViewModel<V>> : StateFragment<V, VM>() {
    override val viewModelOwner = ViewModelOwner.ACTIVITY

    lateinit var navigator: FilterNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ViewModelProvider(activity!!, viewModelFactory)
            .get(FilterNavigator::class.java)
    }
}