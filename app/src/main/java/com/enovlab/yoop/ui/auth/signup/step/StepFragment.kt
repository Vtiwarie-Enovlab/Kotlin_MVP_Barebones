package com.enovlab.yoop.ui.auth.signup.step

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.auth.AuthFragment
import com.enovlab.yoop.ui.auth.signup.SignupViewModel
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel

/**
 * Created by mtosk on 3/6/2018.
 */
abstract class StepFragment <V : StateView, VM : StateViewModel<V>> : AuthFragment<V, VM>() {

    override val viewModelOwner = ViewModelOwner.ACTIVITY
    lateinit var hostViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hostViewModel = ViewModelProvider(activity!!, viewModelFactory)
            .get(SignupViewModel::class.java)
    }
}