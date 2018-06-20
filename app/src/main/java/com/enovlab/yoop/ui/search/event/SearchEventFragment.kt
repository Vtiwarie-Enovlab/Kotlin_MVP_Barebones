package com.enovlab.yoop.ui.search.event

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.api.response.EventSearch
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.filter.search.adapter.SearchDecoration
import com.enovlab.yoop.ui.search.SearchNavigator
import com.enovlab.yoop.ui.search.event.adapter.SearchEventAdapter
import com.enovlab.yoop.utils.ext.delayedTextChangeListener
import kotlinx.android.synthetic.main.fragment_search_city.*

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchEventFragment : StateFragment<SearchEventView, SearchEventViewModel>(), SearchEventView {
    override val vmClass = SearchEventViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    private lateinit var navigator: SearchNavigator
    private val adapter: SearchEventAdapter by lazy { SearchEventAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.query = arguments?.getString(ARG_QUERY)
        navigator = ViewModelProvider(activity!!, viewModelFactory).get(SearchNavigator::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clear.setOnClickListener {
            search.text = null
        }
        back.setOnClickListener {
            navigator.navigateBack.go(true)
        }

        adapter.listener = { navigator.navigateToEventLanding.go(it.eventId) }
        search_list.adapter = adapter
        search_list.addItemDecoration(SearchDecoration(ContextCompat.getDrawable(context!!, R.drawable.decoration_divider_search)!!))

        search.delayedTextChangeListener(viewModel::searchEvents)
        search.requestFocus()
    }

    override fun showEventsSearch(events: List<EventSearch>) {
        adapter.submitList(events)
    }

    override fun showNoResults() {
        adapter.clear()
    }

    override fun showLoadingIndicator(active: Boolean) {
        adapter.isLoading = active
    }

    companion object {
        fun newInstance(query: String?) = SearchEventFragment().apply {
            arguments = Bundle(1).apply {
                putString(ARG_QUERY, query)
            }
        }

        private const val ARG_QUERY = "ARG_QUERY"
    }
}