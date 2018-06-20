package com.enovlab.yoop.ui.auth

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
abstract class AuthFragment<V : StateView, VM : StateViewModel<V>> : StateFragment<V, VM>() {

    lateinit var navigator: AuthNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ViewModelProvider(activity!!, viewModelFactory)
            .get(AuthNavigator::class.java)
    }
}