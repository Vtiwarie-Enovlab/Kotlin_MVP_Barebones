package com.enovlab.yoop.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel

abstract class MainFragment <V : StateView, VM : StateViewModel<V>> : StateFragment<V, VM>() {

    lateinit var navigator: MainNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ViewModelProvider(activity!!, viewModelFactory)
            .get(MainNavigator::class.java)
    }
}