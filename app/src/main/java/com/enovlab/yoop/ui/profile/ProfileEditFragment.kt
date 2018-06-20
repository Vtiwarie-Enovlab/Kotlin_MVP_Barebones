package com.enovlab.yoop.ui.profile

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.base.state.StateViewModel

abstract class ProfileEditFragment <V : StateView, VM : StateViewModel<V>> : StateFragment<V, VM>() {

    lateinit var navigator: ProfileEditNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ViewModelProvider(activity!!, viewModelFactory)
            .get(ProfileEditNavigator::class.java)
    }
}