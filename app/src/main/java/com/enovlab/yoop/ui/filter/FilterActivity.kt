package com.enovlab.yoop.ui.filter

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.filter.details.FilterDetailsFragment
import com.enovlab.yoop.ui.filter.search.SearchCityFragment

/**
 * Created by mtosk on 3/14/2018.
 */
class FilterActivity  : BaseActivity<FilterNavigator>() {
    override val navigatorClass = FilterNavigator::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        navigateToFilterDetails()
    }

    override fun setupNavigation(navigator: FilterNavigator) {
        navigator.navigateBack.observeNavigation(::navigateBack)
        navigator.navigateToFilterDetails.observeNavigation { navigateToFilterDetails() }
        navigator.navigateToSearch.observeNavigation { navigateToSearch() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(CONTAINER)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    private fun navigateToFilterDetails() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, FilterDetailsFragment.newInstance())
            .commit()
    }

    private fun navigateToSearch() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, SearchCityFragment.newInstance())
            .commit()
    }

    companion object {
        private const val CONTAINER = R.id.container_filter
    }
}