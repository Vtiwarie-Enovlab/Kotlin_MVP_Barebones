package com.enovlab.yoop.ui.profile.capture

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class CaptureBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeCaptureFragment(): CaptureFragment

    @Binds
    @IntoMap
    @ViewModelKey(CaptureViewModel::class)
    internal abstract fun bindCaptureViewModel(viewModel: CaptureViewModel): ViewModel

}