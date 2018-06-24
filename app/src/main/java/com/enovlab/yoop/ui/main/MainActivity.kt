package com.enovlab.yoop.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.state.StateActivity

class MainActivity : StateActivity<MainView, MainViewModel, MainNavigator>(), MainView {
    override val navigatorClass = MainNavigator::class.java
    override val vmClass = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(MainViewModel::class.java)
    }

    override fun setupNavigation(navigator: MainNavigator) {
    }

    companion object {
        private const val CONTAINER = R.id.container_main
    }
}
