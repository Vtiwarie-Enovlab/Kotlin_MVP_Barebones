package com.enovlab.yoop.ui.settings.support

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.settings.SettingsFragment
import com.zopim.android.sdk.prechat.ZopimChatActivity
import kotlinx.android.synthetic.main.fragment_settings_support.*
import kotlinx.android.synthetic.main.layout_settings_app_bar.*
import zendesk.support.guide.HelpCenterActivity
import zendesk.support.guide.HelpCenterUiConfig
import zendesk.support.request.RequestActivity

class SupportFragment : SettingsFragment<SupportView, SupportViewModel>(), SupportView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = SupportViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings_back.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        settings_title.setText(R.string.settings_title_support)

        support_faq.setOnClickListener {
            HelpCenterActivity.builder()
                .withShowConversationsMenuButton(false)
                .withContactUsButtonVisible(false)
                .show(activity!!)
        }
        support_chat.setOnClickListener {
            startActivity(Intent(context, ZopimChatActivity::class.java))
        }
        support_email.setOnClickListener {

        }
        support_feedback.setOnClickListener {
            RequestActivity.builder().show(activity!!)
        }
    }

    companion object {
        fun newInstance() = SupportFragment()
    }
}