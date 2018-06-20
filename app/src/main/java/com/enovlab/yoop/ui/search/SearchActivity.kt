package com.enovlab.yoop.ui.search

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.event.EventActivity
import com.enovlab.yoop.ui.search.event.SearchEventFragment

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchActivity : BaseActivity<SearchNavigator>() {
    override val navigatorClass = SearchNavigator::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        onNewIntent(intent)
    }

    override fun setupNavigation(navigator: SearchNavigator) {
        navigator.navigateBack.observeNavigation(::navigateBack)
        navigator.navigateToEventLanding.observeNavigation { navigateToEventLanding(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val query = when {
            intent?.action == Intent.ACTION_SEARCH -> intent.getStringExtra(SearchManager.QUERY)
            else -> null
        }
        navigateToSearchEvent(query)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_DISCOVER_MORE && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    private fun navigateToSearchEvent(query: String? = null) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, SearchEventFragment.newInstance(query))
            .commit()
    }

    private fun navigateToEventLanding(eventId: String) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra(EventActivity.ARG_EVENT_ID, eventId)
        startActivityForResult(intent, RC_DISCOVER_MORE)
    }

    companion object {
        private const val CONTAINER = R.id.container_search
        private const val RC_DISCOVER_MORE = 5465
    }
}