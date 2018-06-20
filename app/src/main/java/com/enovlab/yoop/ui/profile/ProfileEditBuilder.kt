package com.enovlab.yoop.ui.profile

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.profile.capture.CaptureBuilder
import com.enovlab.yoop.ui.profile.details.ProfileDetailsBuilder
import com.enovlab.yoop.ui.profile.intro.IntroBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class ProfileEditBuilder {

    @ContributesAndroidInjector(modules = [
        ProfileDetailsBuilder::class,
        CaptureBuilder::class,
        IntroBuilder::class
    ])
    internal abstract fun contributeProfileEditActivity(): ProfileEditActivity

    @Binds
    @IntoMap
    @ViewModelKey(ProfileEditNavigator::class)
    internal abstract fun bindProfileEditNavigator(navigator: ProfileEditNavigator): ViewModel
}
