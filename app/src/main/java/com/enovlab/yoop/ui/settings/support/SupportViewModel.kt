package com.enovlab.yoop.ui.settings.support

import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import zendesk.core.AnonymousIdentity
import zendesk.core.Zendesk
import zendesk.support.Support
import javax.inject.Inject
import com.zopim.android.sdk.api.ZopimChat
import com.zopim.android.sdk.model.VisitorInfo



class SupportViewModel
@Inject constructor(private val repository: UserRepository) : StateViewModel<SupportView>() {

    override fun start() {
        disposables += repository.user().subscribe({ user ->
            val identity = AnonymousIdentity.Builder()
                .withEmailIdentifier(user.email)
                .withNameIdentifier("${user.firstName} ${user.lastName}")
                .build()

            Zendesk.INSTANCE.setIdentity(identity)
            Support.INSTANCE.init(Zendesk.INSTANCE)

            val visitorData = VisitorInfo.Builder()
                .name("${user.firstName} ${user.lastName}")
                .email(user.email)
                .build()

            ZopimChat.setVisitorInfo(visitorData)
        }, { error ->
            Timber.e(error)
        })
    }
}