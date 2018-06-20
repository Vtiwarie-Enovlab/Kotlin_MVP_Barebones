package com.enovlab.yoop.ui.main.mytickets

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxFragment
import com.enovlab.yoop.ui.main.mytickets.requested.RequestedFragment
import com.enovlab.yoop.ui.main.mytickets.secured.SecuredFragment
import com.enovlab.yoop.utils.ext.listener
import kotlinx.android.synthetic.main.fragment_my_tickets.*
import kotlinx.android.synthetic.main.layout_inbox_notification_badge.view.*

class MyTicketsFragment : StateFragment<MyTicketsView, MyTicketsViewModel>(), MyTicketsView {

    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = MyTicketsViewModel::class.java

    private lateinit var navigation: MyTicketsNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigation = arguments?.getString(ARG_NAVIGATION)?.
            let { MyTicketsNavigation.valueOf(it) } ?: MyTicketsNavigation.REQUESTED
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inboxTab = my_tickets_tabs.newTab()
        inboxTab.customView = LayoutInflater.from(context!!).inflate(R.layout.layout_inbox_notification_badge, null)

        with(my_tickets_tabs) {
            addTab(my_tickets_tabs.newTab().setText(R.string.my_tickets_tab_secured))
            addTab(my_tickets_tabs.newTab().setText(R.string.my_tickets_tab_requested))
            addTab(inboxTab)
            listener { position ->
                when (position) {
                    0 -> navigateToSecured() // secured
                    1 -> navigateToRequested() // requested
                    2 -> navigateToInbox()// inbox
                }
                updateInboxTextColor(position == 2)
            }
        }

        my_tickets_tabs.getTabAt(navigation.ordinal)?.select()
    }

    override fun showNotificationsCountActive(active: Boolean) {
        val tab = my_tickets_tabs.getTabAt(2)!!
        tab.customView?.inbox_badge?.isVisible = active
    }

    override fun showNotificationsCount(count: String) {
        val tab = my_tickets_tabs.getTabAt(2)!!
        tab.customView?.inbox_badge?.text = count
    }

    private fun updateInboxTextColor(selected: Boolean) {
        val tab = my_tickets_tabs.getTabAt(2)!!
        tab.customView?.inbox_title?.setTextColor(ContextCompat.getColor(context!!, when {
            selected -> R.color.color_white
            else -> R.color.color_white_alpha_70
        }))
    }

    private fun navigateToSecured() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, SecuredFragment.newInstance())
            .commit()
    }

    private fun navigateToRequested() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, RequestedFragment.newInstance())
            .commit()
    }

    private fun navigateToInbox() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, InboxFragment.newInstance())
            .commit()
    }

    companion object {
        private const val CONTAINER = R.id.my_tickets_container
        private const val ARG_NAVIGATION = "ARG_NAVIGATION"

        fun newInstance(navigation: MyTicketsNavigation = MyTicketsNavigation.REQUESTED): MyTicketsFragment {
            return MyTicketsFragment().apply {
                arguments = Bundle(1).apply {
                    putString(ARG_NAVIGATION, navigation.name)
                }
            }
        }
    }
}